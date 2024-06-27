package org.vstu.meaningtree.languages.viewers;

import org.vstu.meaningtree.languages.PythonSpecialTreeTransformations;
import org.vstu.meaningtree.languages.utils.Tab;
import org.vstu.meaningtree.nodes.*;
import org.vstu.meaningtree.nodes.bitwise.*;
import org.vstu.meaningtree.nodes.comparison.*;
import org.vstu.meaningtree.nodes.declarations.*;
import org.vstu.meaningtree.nodes.definitions.ClassDefinition;
import org.vstu.meaningtree.nodes.definitions.Definition;
import org.vstu.meaningtree.nodes.definitions.FunctionDefinition;
import org.vstu.meaningtree.nodes.definitions.MethodDefinition;
import org.vstu.meaningtree.nodes.identifiers.QualifiedIdentifier;
import org.vstu.meaningtree.nodes.identifiers.ScopedIdentifier;
import org.vstu.meaningtree.nodes.identifiers.SimpleIdentifier;
import org.vstu.meaningtree.nodes.literals.*;
import org.vstu.meaningtree.nodes.logical.NotOp;
import org.vstu.meaningtree.nodes.logical.ShortCircuitAndOp;
import org.vstu.meaningtree.nodes.logical.ShortCircuitOrOp;
import org.vstu.meaningtree.nodes.math.*;
import org.vstu.meaningtree.nodes.statements.*;
import org.vstu.meaningtree.nodes.types.*;
import org.vstu.meaningtree.nodes.unary.*;
import org.vstu.meaningtree.nodes.utils.WildcardImport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedMap;


public class PythonViewer extends Viewer {
    private record TaggedBinaryComparisonOperand(Expression wrapped, boolean hasEqual) { }

    @Override
    public String toString(Node node) {
        Tab tab = new Tab();
        return toString(node, tab);
    }

    public String toString(Node ... nodes) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < nodes.length; i++) {
            builder.append(toString(nodes[i]));
            if (i != nodes.length - 1) {
                builder.append("\n");
            }
        }
        return builder.toString();
    }

    public String toString(Node node, Tab tab) {
        return switch (node) {
            case ProgramEntryPoint programEntryPoint -> entryPointToString(programEntryPoint, tab);
            case BinaryComparison cmpNode -> comparisonToString(cmpNode);
            case BinaryExpression binaryExpression -> binaryOpToString(binaryExpression);
            case IfStatement ifStatement -> conditionToString(ifStatement, tab);
            case UnaryExpression exprNode -> unaryToString(exprNode);
            case CompoundStatement exprNode -> blockToString(exprNode, tab);
            case CompoundComparison compound -> compoundComparisonToString(compound);
            case Identifier identifier -> identifierToString(identifier);
            case IndexExpression indexExpr -> String.format("%s[%s]", toString(indexExpr.getExpr()), toString(indexExpr.getIndex()));
            case MemberAccess memAccess -> String.format("%s.%s", toString(memAccess.getExpression()), toString(memAccess.getMember()));
            case TernaryOperator ternary -> String.format("%s ? %s : %s", toString(ternary.getCondition()), toString(ternary.getThenExpr()), toString(ternary.getElseExpr()));
            case ParenthesizedExpression paren -> String.format("(%s)", toString(paren.getExpression()));
            case ObjectNewExpression newExpr -> callsToString(newExpr);
            case ArrayNewExpression newExpr -> callsToString(newExpr);
            case FunctionCall funcCall -> callsToString(funcCall);
            case BreakStatement breakStmt -> "break";
            case DeleteStatement delStmt -> String.format("del %s", toString(delStmt.getTarget()));
            case Range range -> rangeToString(range);
            case ContinueStatement continueStatement -> "continue";
            case Comment comment -> commentToString(comment);
            case Literal literal -> literalToString(literal);
            case AssignmentExpression assignmentExpr -> assignmentExpressionToString(assignmentExpr);
            case AssignmentStatement assignmentStatement -> assignmentToString(assignmentStatement);
            case VariableDeclaration varDecl -> variableDeclarationToString(varDecl);
            case ForLoop forLoop -> loopToString(forLoop, tab);
            case WhileLoop whileLoop -> loopToString(whileLoop, tab);
            case DoWhileLoop doWhileLoop -> loopToString(doWhileLoop, tab);
            case SwitchStatement switchStmt -> loopToString(switchStmt, tab);
            case MethodDefinition methodDef -> functionToString(methodDef, tab);
            case FunctionDefinition funcDef -> functionToString(funcDef, tab);
            case ClassDeclaration classDecl -> classDeclToString(classDecl, tab);
            case ClassDefinition classDef -> classToString(classDef, tab);
            case FunctionDeclaration funcDecl -> functionDeclarationToString(funcDecl, tab);
            case Import importStmt -> importToString(importStmt);
            case ExpressionStatement exprStmt -> toString(exprStmt.getExpression());
            case StatementSequence stmtSequence -> {
                if (stmtSequence.isOnlyAssignments()) {
                    yield assignmentToString(stmtSequence);
                } else {
                    yield String.join(", ", stmtSequence.getStatements().stream().map((Statement nd) -> toString(nd, tab)).toList().toArray(new String[0]));
                }
            }
            case null, default -> throw new RuntimeException("Unsupported tree element");
        };
    }

    private String identifierToString(Identifier identifier) {
        if (identifier instanceof WildcardImport) {
            return "*";
        } else if (identifier instanceof SimpleIdentifier ident) {
            return ident.getName();
        } else if (identifier instanceof ScopedIdentifier scopedIdent) {
            return String.join(".", scopedIdent.getScopeResolution().stream().map(this::identifierToString).toList().toArray(new String[0]));
        } else if (identifier instanceof QualifiedIdentifier qualifiedIdent) {
            return String.format("%s.%s", identifierToString(qualifiedIdent.getScope()), identifierToString(qualifiedIdent.getMember()));
        }
        return identifier.toString();
    }

    private String importToString(Import importStmt) {
        StringBuilder builder = new StringBuilder();
        if (importStmt.hasMember()) {
            builder.append(String.format("from %s import %s", toString(importStmt.getScope()), toString(importStmt.getMember())));
        } else {
            builder.append(String.format("import %s", toString(importStmt.getScope())));
        }
        if (importStmt.hasAlias()) {
            builder.append(String.format(" as %s", toString(importStmt.getAlias())));
        }
        return builder.toString();
    }

    private String functionDeclarationToString(FunctionDeclaration decl, Tab tab) {
        if (decl instanceof MethodDeclaration method) {
            return functionToString(new MethodDefinition(method, new CompoundStatement()), tab);
        }
        return functionToString(new FunctionDefinition(decl, new CompoundStatement()), tab);
    }

    private String classToString(ClassDefinition def, Tab tab) {
        StringBuilder builder = new StringBuilder();
        ClassDeclaration decl = (ClassDeclaration) def.getDeclaration();
        if (decl.getParents().isEmpty()) {
            builder.append(String.format("class %s:\n", toString(decl.getName())));
        } else {
            builder.append(String.format("class %s(%s):\n", toString(decl.getName()), String.join(",", decl.getParents().stream().map(this::typeToString).toList().toArray(new String[0]))));
        }
        builder.append(toString(def.getBody(), tab.up()));
        return builder.toString();
    }

    private String classDeclToString(ClassDeclaration decl, Tab tab) {
        return toString(new ClassDefinition(decl, new CompoundStatement()), tab);
    }

    private String functionToString(Definition func, Tab tab) {
        StringBuilder function = new StringBuilder();
        FunctionDeclaration decl = (FunctionDeclaration) func.getDeclaration();
        for (Annotation anno : decl.getAnnotations()) {
            if (anno.getArguments().length == 0) {
                function.append(String.format("@%s(%s)\n", toString(anno.getName()), argumentsToString(Arrays.asList(anno.getArguments()))));
            } else {
                function.append(String.format("@%s\n", toString(anno.getName())));
            }
        }
        function.append("def ");
        function.append(decl.getName());
        function.append("(");
        List<DeclarationArgument> declArgs = decl.getArguments();
        for (int i = 0; i < declArgs.size(); i++) {
            DeclarationArgument arg = declArgs.get(i);
            function.append(toString(arg.getName()));
            if (!(arg.getType() instanceof UnknownType) && arg.getType() != null) {
                function.append(": ");
                function.append(typeToString(arg.getType()));
            } else if (i == 0 && decl instanceof MethodDeclaration method) {
                function.append(String.format(": %s", typeToString(method.getOwner())));
            }
            function.append(", ");
        }
        function.append(")");
        if (decl.getReturnType() != null && !(decl.getReturnType() instanceof UnknownType)) {
            function.append("->");
            function.append(typeToString(decl.getReturnType()));
        }
        function.append(":\n");
        if (func instanceof MethodDefinition methodDef) {
            function.append(toString(methodDef.getBody(), tab.up()));
        } else if (func instanceof FunctionDefinition funcDef) {
            function.append(toString(funcDef.getBody(), tab.up()));
        }
        return function.toString();
    }

    private String assignmentToString(StatementSequence stmtSequence) {
        if (stmtSequence.isOnlyAssignments()) {
            AugmentedAssignmentOperator augOp = ((AssignmentStatement) stmtSequence.getStatements().getFirst()).getAugmentedOperator();
            String operator = switch (augOp) {
                case ADD -> "+=";
                case SUB -> "-=";
                case MUL -> "*=";
                case DIV -> "/=";
                case FLOOR_DIV -> "//=";
                case BITWISE_AND -> "&=";
                case BITWISE_OR -> "|=";
                case BITWISE_XOR -> "^=";
                case BITWISE_SHIFT_LEFT -> "<<=";
                case BITWISE_SHIFT_RIGHT -> ">>=";
                case MOD -> "%=";
                case POW -> "**=";
                default -> "=";
            };
            List<Expression> lvalues = new ArrayList<>();
            List<Expression> rvalues = new ArrayList<>();
            for (Statement stmt : stmtSequence.getStatements()) {
                AssignmentStatement assignment = (AssignmentStatement) stmt;
                lvalues.add(assignment.getLValue());
                rvalues.add(assignment.getRValue());
            }
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < lvalues.size(); i++) {
                builder.append(toString(lvalues.get(i)));
            }
            builder.append(' ');
            builder.append(operator);
            builder.append(' ');
            for (int i = 0; i < lvalues.size(); i++) {
                builder.append(toString(lvalues.get(i)));
            }
            return builder.toString();
        } else {
            throw new RuntimeException("Invalid usage of assignmentToString method");
        }
    }

    private String entryPointToString(ProgramEntryPoint programEntryPoint, Tab tab) {
        IfStatement entryPointIf = null;
        if (programEntryPoint.hasEntryPoint()) {
            Node entryPointNode = programEntryPoint.getEntryPoint();
            if (entryPointNode instanceof FunctionDefinition func) {
                Identifier ident;
                FunctionDeclaration funcDecl = (FunctionDeclaration) func.getDeclaration();
                if (funcDecl instanceof MethodDeclaration method) {
                    ident = new ScopedIdentifier(method.getOwner().getName(), method.getName());
                } else {
                    ident = func.getName();
                }
                //NOTE: default behaviour - ignore arguments in call main function
                List<Expression> nulls = new ArrayList<>();
                for (DeclarationArgument arg : funcDecl.getArguments()) {
                    if (!arg.isListUnpacking()) {
                        nulls.add(new NullLiteral());
                    }
                }
                FunctionCall funcCall = new FunctionCall(ident, nulls.toArray(new Expression[0]));
                entryPointIf = new IfStatement(new EqOp(new SimpleIdentifier("__name__"), StringLiteral.fromUnescaped("__main__", StringLiteral.Type.NONE)), new CompoundStatement(funcCall),null);
            } else if (entryPointNode instanceof CompoundStatement compound) {
                entryPointIf = new IfStatement(new EqOp(new SimpleIdentifier("__name__"), StringLiteral.fromUnescaped("__main__", StringLiteral.Type.NONE)), compound,null);
            }
        }
        List<Node> nodes = new ArrayList<>(programEntryPoint.getBody());
        if (entryPointIf != null) {
            nodes.add(entryPointIf);
        }
        return nodeListToString(nodes, tab);
    }

    private String loopToString(Statement stmt, Tab tab) {
        StringBuilder builder = new StringBuilder();
        if (stmt instanceof RangeForLoop rangeFor) {
            builder.append(String.format("for %s in range(%s, %s, %s):\n",
                    toString(rangeFor.getIdentifier()),
                    toString(rangeFor.getStart()),
                    toString(rangeFor.getEnd()),
                    toString(rangeFor.getStep())
            ));
            builder.append(toString(rangeFor.getBody(), tab));
        } else if (stmt instanceof GeneralForLoop generalFor) {
            return toString(PythonSpecialTreeTransformations.representGeneralFor(generalFor));
        } else if (stmt instanceof DoWhileLoop doWhile) {
            return toString(PythonSpecialTreeTransformations.representDoWhile(doWhile));
        } else if (stmt instanceof WhileLoop whileLoop) {
            builder.append(String.format("while %s:\n", toString(whileLoop.getCondition())));
            builder.append(toString(whileLoop.getBody(), tab));
        } else if (stmt instanceof ForEachLoop forEachLoop) {
            List<Expression> identifiers = new ArrayList<>();
            for (VariableDeclarator decl : forEachLoop.getItem().getDeclarators()) {
                identifiers.add(decl.getIdentifier());
            }
            builder.append(String.format("for %s in %s:\n", argumentsToString(identifiers), toString(forEachLoop.getExpression())));
            builder.append(toString(forEachLoop.getBody(), tab));
        } else if (stmt instanceof SwitchStatement switchStmt) {
            tab = tab.up();
            builder.append(String.format("match %s:\n", switchStmt.getTargetExpression()));
            for (ConditionBranch caseBranch : switchStmt.getCases()) {
                builder.append(String.format("%scase %s:\n%s\n\n", tab, toString(caseBranch.getCondition()), toString(caseBranch.getBody(), tab)));
            }
            if (switchStmt.hasDefaultCase()) {
                builder.append(String.format("%scase _:\n%s\n", tab, toString(switchStmt.getDefaultCase(), tab)));
            }
        }
        return builder.toString();
    }

    private String variableDeclarationToString(VariableDeclaration varDecl) {
        StringBuilder lValues = new StringBuilder();
        StringBuilder rValues = new StringBuilder();
        VariableDeclarator[] decls = varDecl.getDeclarators();
        for (int i = 0; i < decls.length; i++) {
            lValues.append(toString(decls[i].getIdentifier()));
            //NEED DISCUSSION, see typeToString notes
            if (!(varDecl.getType() instanceof UnknownType) && varDecl.getType() != null) {
                lValues.append(String.format(": %s", typeToString(varDecl.getType())));
            }
            if (decls[i].hasInitialization()) {
                decls[i].getRValue().ifPresent(rValues::append);
            } else {
                rValues.append("None");
            }
            if (i != decls.length - 1) {
                lValues.append(", ");
                rValues.append(", ");
            }
        }
        return String.format("%s = %s", lValues, rValues);
    }

    private String typeToString(Type type) {
        //NOTE: python 3.9+ typing support, without using typing library
        if (type instanceof IntType) {
            return "int";
        } else if (type instanceof FloatType) {
            return "float";
        } else if (type instanceof DictionaryType dictType) {
            if (dictType.getKeyType() != null && dictType.getValueType() != null) {
                return String.format("dict[%s, %s]", typeToString(dictType.getKeyType()), typeToString(dictType.getValueType()));
            }
            return "dict";
        } else if (type instanceof StringType) {
            return "str";
        } else if (type instanceof BooleanType) {
            return "bool";
        } else if (type instanceof ListType listType) {
            if (listType.getItemType() != null) {
                return String.format("list[%s]",  typeToString(listType.getItemType()));
            }
            return "list";
        } else if (type instanceof ArrayType listType) {
            if (listType.getItemType() != null) {
                return String.format("list[%s]",  typeToString(listType.getItemType()));
            }
            return "list";
        } else if (type instanceof SetType setType) {
            if (setType.getItemType() != null) {
                return String.format("set[%s]",  typeToString(setType.getItemType()));
            }
            return "set";
        } else if (type instanceof UnmodifiableListType tupleType) {
            if (tupleType.getItemType() != null) {
                return String.format("tuple[%s]",  typeToString(tupleType.getItemType()));
            }
            return "tuple";
        } else if (type instanceof GenericUserType generic) {
            return String.format("%s[%s]", generic.getName().toString(), String.join(",", Arrays.stream(generic.getTypeParameters()).map(this::typeToString).toList().toArray(new String[0])));
        } else if (type instanceof UserType userType) {
            return userType.getName().toString();
        }
        return "object";
    }

    private String assignmentExpressionToString(AssignmentExpression expr) {
        return String.format("%s := %s", toString(expr.getLValue()), toString(expr.getRValue()));
    }

    private String assignmentToString(AssignmentStatement stmt) {
        AugmentedAssignmentOperator augOp = stmt.getAugmentedOperator();
        String operator = switch (augOp) {
            case ADD -> "+=";
            case SUB -> "-=";
            case MUL -> "*=";
            case DIV -> "/=";
            case FLOOR_DIV -> "//=";
            case BITWISE_AND -> "&=";
            case BITWISE_OR -> "|=";
            case BITWISE_XOR -> "^=";
            case BITWISE_SHIFT_LEFT -> "<<=";
            case BITWISE_SHIFT_RIGHT -> ">>=";
            case MOD -> "%=";
            case POW -> "**=";
            default -> "=";
        };
        return String.format("%s %s %s", toString(stmt.getLValue()), operator, toString(stmt.getRValue()));
    }

    private String literalToString(Literal literal) {
        if (literal instanceof NumericLiteral numLiteral) {
            return numLiteral.getStringValue();
        } else if (literal instanceof StringLiteral strLiteral) {
            String prefix;
            switch (strLiteral.getStringType()) {
                case INTERPOLATION -> prefix = "f";
                case RAW ->  prefix = "r";
                default -> prefix = "";
            }
            return String.format("%s\"%s\"", prefix, strLiteral.getEscapedValue());
        } else if (literal instanceof BoolLiteral bool) {
           if (bool.getValue()) {
               return "True";
           } else {
               return "False";
           }
        } else if (literal instanceof ListLiteral list) {
            return String.format("[%s]", argumentsToString(list.getList()));
        } else if (literal instanceof SetLiteral set) {
            return String.format("{%s}", argumentsToString(set.getList()));
        } else if (literal instanceof UnmodifiableListLiteral tuple) {
            return String.format("(%s)", argumentsToString(tuple.getList()));
        } else if (literal instanceof DictionaryLiteral dict) {
            SortedMap<Expression, Expression> map = dict.getDictionary();
            StringBuilder builder = new StringBuilder();
            builder.append('{');
            for (Expression key : map.keySet()) {
                builder.append(String.format("%s : %s,", key, map.get(key)));
            }
            builder.append("}", builder.length() - 1, builder.length());
            return builder.toString();
        } else {
            return "None";
        }
    }

    private String commentToString(Comment comment) {
        if (comment.isMultiline()) {
            return String.format("\"\"\"\n%s\n\"\"\"", comment.getEscapedContent());
        } else {
            return String.format("# %s", comment.getEscapedContent());
        }
    }

    private String rangeToString(Range range) {
        StringBuilder builder = new StringBuilder();
        if (range.hasStart()) {
            builder.append(toString(range.getStart()));
        }
        builder.append(':');
        if (range.hasStop()) {
            builder.append(toString(range.getStop()));
        }
        if (range.hasStep()) {
            builder.append(':');
            builder.append(toString(range.getStep()));
        }
        return builder.toString();
    }

    private String binaryOpToString(BinaryExpression node) {
        String pattern = "";
        if (node instanceof AddOp) {
            pattern = "%s + %s";
        } else if (node instanceof SubOp) {
            pattern = "%s - %s";
        } else if (node instanceof MulOp) {
            pattern = "%s * %s";
        } else if (node instanceof DivOp) {
            pattern = "%s / %s";
        } else if (node instanceof PowOp) {
            pattern = "%s ** %s";
        } else if (node instanceof FloorDivOp) {
            pattern = "%s // %s";
        } else if (node instanceof ModOp) {
            pattern = "%s %% %s";
        } else if (node instanceof BitwiseAndOp) {
            pattern = "%s & %s";
        } else if (node instanceof BitwiseOrOp) {
            pattern = "%s | %s";
        } else if (node instanceof RightShiftOp) {
            pattern = "%s >> %s";
        } else if (node instanceof LeftShiftOp) {
            pattern = "%s << %s";
        } else if (node instanceof XorOp) {
            pattern = "%s ^ %s";
        } else if (node instanceof ShortCircuitAndOp) {
            Node result = detectCompoundComparison(node);
            if (result instanceof CompoundComparison) {
                return compoundComparisonToString((CompoundComparison) result);
            } else if (result instanceof ShortCircuitAndOp resultOp) {
                return String.format("%s and %s", toString(resultOp.getLeft()), toString(resultOp.getRight()));
            }
        } else if (node instanceof ShortCircuitOrOp) {
            pattern = "%s or %s";
        }
        return String.format(pattern, toString(node.getLeft()), toString(node.getRight()));
    }

    private String callsToString(Node node) {
        if (node instanceof ArrayNewExpression newExpr) {
            if (!newExpr.getInitialArray().isEmpty()) {
                return String.format("[%s]", argumentsToString(newExpr.getInitialArray()));
            } else {
                return String.format("[%s for _ in range(%s)]", String.format("%s()", newExpr.getType()), toString(newExpr.getDimension()));
            }
        } else if (node instanceof ObjectNewExpression newExpr) {
            return String.format("%s(%s)", newExpr.getType(), argumentsToString(newExpr.getConstructorArguments()));
        } else if (node instanceof FunctionCall funcCall) {
            return String.format("%s(%s)", funcCall.getFunctionName(), argumentsToString(funcCall.getArguments()));
        } else {
            throw new RuntimeException("Not a callable object");
        }
    }

    private String argumentsToString(List<Expression> expressions) {
        String[] exprStrings = new String[expressions.size()];
        for (int i = 0; i < exprStrings.length; i++) {
            exprStrings[i] = toString(expressions.get(i));
        }
        return String.join(", ", exprStrings);
    }

    private String conditionToString(IfStatement node, Tab tab) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < node.getBranches().size(); i++) {
            String pattern = "elif %s:\n%s\n";
            if (i == 0) {
                pattern = "if %s:\n%s\n";
            }
            ConditionBranch branch = node.getBranches().get(i);
            sb.append(String.format(pattern, branch.getCondition(), toString(branch.getBody(), tab)));
        }
        sb.append(String.format("else:%s\n", toString(node.getElseBranch(), tab)));
        return sb.toString();
    }

    private String unaryToString(UnaryExpression node) {
        String pattern = "";
        if (node instanceof UnaryPlusOp) {
            pattern = "+%s";
        } else if (node instanceof UnaryMinusOp) {
            pattern = "-%s";
        } else if (node instanceof NotOp) {
            pattern = "not %s";
        } else if (node instanceof InversionOp) {
            pattern = "~%s";
        } else if (node instanceof PostfixDecrementOp || node instanceof PrefixDecrementOp) {
            pattern = "%s -= 1";
        } else if (node instanceof PostfixIncrementOp || node instanceof PrefixIncrementOp) {
            pattern = "%s += 1";
        }
        return String.format(pattern, toString(node.getArgument()));
    }

    private String blockToString(CompoundStatement node, Tab tab) {
        StringBuilder builder = new StringBuilder();
        tab = tab.up();
        if (node.getNodes().length == 0) {
            return "pass";
        }
        for (Node child : node) {
            builder.append(tab);
            builder.append(toString(node, tab));
            builder.append('\n');
        }
        return builder.toString();
    }

    private String nodeListToString(List<Node> nodes, Tab tab) {
        StringBuilder builder = new StringBuilder();
        if (nodes.isEmpty()) {
            return "pass";
        }
        for (Node child : nodes) {
            builder.append(tab);
            builder.append(toString(child, tab));
            builder.append('\n');
        }
        return builder.toString();
    }

    // COMPARISONS SECTION //
    //TODO: very unstable code, needed more tests
    private Node detectCompoundComparison(Node expressionNode) {
        if (expressionNode instanceof ShortCircuitAndOp op) {
            // Список выражений, которые не будут участвовать в составном сравнении.
            // Например: a == b, a != b или выражения отличные от сравнения
            ArrayList<Expression> secondary = new ArrayList<>();
            // Список сравнений, который непосредственно будет сгруппирован
            // в одно составное сравнение
            ArrayList<BinaryComparison> primary = new ArrayList<>();
            // Разбираем выражение AND на нужные для составного сравнения и ненужные
            // Те, что secondary будут просто присоединены к итоговому выражению с помощью AND
            _collectCompoundStatementElements(op, primary, secondary);
            ArrayList<BinaryComparison> primaryCopy = new ArrayList<>(primary);
            /*
            Разбираем все сравнения на операнды. Операнды заносим в специальный массив
            Индекс массива считается весом операнда
            Веса увеличиваются так, чтобы итоговые операнды можно было соединить знаком < или <=

            После разбора операнды становятся группами так, что между ними есть свободная клетка
            Например: a <= b and b >= c and k <= m
            Массив с весами: abc km

            Или другой пример: a <= b and k <= m and b >= c
            Массив с весами: ab km c

            Элементы массивы - не выражения, а обертки, которые представляют информацию о знаке сравнения.
            Каждый элемент представляет информацию о знаке, который будет стоять ПЕРЕД ним (< или <=)
             */
            TaggedBinaryComparisonOperand[] expressions = new TaggedBinaryComparisonOperand[3 * primary.size()];
            for (int i = 1; i < expressions.length && !primary.isEmpty(); i+=3) {
                BinaryComparison expr = primary.removeFirst();
                Expression leftOperand = expr.getLeft();
                Expression rightOperand = expr.getRight();
                // Ищем, если элемент уже имеет вес
                int leftOperandFoundWeight = _findSameExpression(leftOperand, expressions);
                int rightOperandFoundWeight = _findSameExpression(rightOperand, expressions);

                int leftWeight, rightWeight;
                if (expr instanceof LtOp || expr instanceof LeOp) {
                    leftWeight = leftOperandFoundWeight != -1 ? leftOperandFoundWeight : i;
                    rightWeight = rightOperandFoundWeight != -1 ? rightOperandFoundWeight : i + 1;
                } else {
                    leftWeight = leftOperandFoundWeight != -1 ? leftOperandFoundWeight : i + 1;
                    rightWeight = rightOperandFoundWeight != -1 ? rightOperandFoundWeight : i;
                }

                // Корректируем положение операнда => вес в массиве, исходя из данных сравнения
                if (expr instanceof LtOp || expr instanceof LeOp) {
                    if (leftWeight < rightWeight) {
                        expressions[leftWeight] = new TaggedBinaryComparisonOperand(leftOperand, false);
                        expressions[rightWeight] = new TaggedBinaryComparisonOperand(rightOperand, expr instanceof LeOp);
                    } else {
                        expressions[leftWeight] = new TaggedBinaryComparisonOperand(rightOperand, false);
                        expressions[rightWeight] = new TaggedBinaryComparisonOperand(leftOperand, expr instanceof LeOp);
                    }
                } else if (expr instanceof GeOp || expr instanceof GtOp) {
                    if (leftWeight > rightWeight) {
                        expressions[leftWeight] = new TaggedBinaryComparisonOperand(leftOperand, false);
                        expressions[rightWeight] = new TaggedBinaryComparisonOperand(rightOperand, expr instanceof GeOp);
                    } else {
                        expressions[leftWeight] = new TaggedBinaryComparisonOperand(rightOperand, false);
                        expressions[rightWeight] = new TaggedBinaryComparisonOperand(leftOperand, expr instanceof GeOp);
                    }
                }
            }

            /*
            Теперь необходимо, исходя из сравнений, пересчитать веса операндов так,
            чтобы они сгруппировались в составные сравнения

            Вероятно, не получится сгруппировать операнды так, чтобы они образовали единое составное сравнение
            Тогда, пустая клетка (со значением null) будет разделителем составных присваиваний

            Краткий алгоритм:

            Для каждой пары операндов сравнения:
            1. Выделим левую группу и ее интервал [leftA, leftB], содержащий один операнд (левый или правый) текущей пары операндов
            2. Выделим правую группу и ее интервал [rightA, rightB], содержащий один операнд (левый или правый) текущей пары операндов
            3. Попробуем расположить группы рядом, объединив их в интервал [leftA, rightB]
            4. Для этого поэтапно переносим элементы из группы right в конец группы left.
            */
            for (BinaryComparison cmp : primaryCopy) {
                Expression leftOperand = cmp.getLeft();
                Expression rightOperand = cmp.getRight();

                int leftWeight = _findSameExpression(leftOperand, expressions);
                int rightWeight = _findSameExpression(rightOperand, expressions);

                if (Math.abs(leftWeight - rightWeight) == 1) {
                    continue;
                }

                if (leftWeight > rightWeight) {
                    int tmp = leftWeight;
                    leftWeight = rightWeight;
                    rightWeight = tmp;
                }

                int leftA, leftB, rightA, rightB;
                for (leftA = leftWeight; leftA > 0 && expressions[leftA] != null; leftA--);
                for (leftB = leftWeight; leftB < expressions.length && expressions[leftB] != null; leftB++);

                for (rightA = rightWeight; rightA > 0 && expressions[rightA] != null; rightA--);
                for (rightB = rightWeight; rightB < expressions.length && expressions[rightB] != null; rightB++);

                rightA++;
                rightB--;
                leftA++;
                leftB--;

                if (rightA >= leftA && rightA <= leftB) {
                    continue;
                }

                for (int i = leftB + 1; i <= leftB + (rightB - rightA + 1); i++) {
                    expressions[i] = expressions[rightA];
                    expressions[rightA] = null;
                    TaggedBinaryComparisonOperand tmp, prev;
                    prev = expressions[i + 1];
                    for (int j = i + 1; j < rightA; j++) {
                        tmp = expressions[j + 1];
                        expressions[j + 1] = prev;
                        prev = tmp;
                    }
                    expressions[i + 1] = null;
                    rightA++;
                }

            }
            /*
            Будущие составные сравнения сформированы визуально, осталось собрать их в узел дерева
            Также добавятся к составным сравнениям и отброшенные ранее secondary узлы

            Соберем составные сравнения в узел,
            учитывая что группу операндов одного составного сравнения
            отделяет любое количество пустых клеток с null
             */
            List<BinaryComparison> tempComparisons = new ArrayList<>();
            List<CompoundComparison> compounds = new ArrayList<>();
            for (int i = 1; i < expressions.length - 1; i++) {
                if (expressions[i] != null && expressions[i + 1] != null){
                    if (expressions[i + 1].hasEqual) {
                        tempComparisons.add(new LeOp(expressions[i].wrapped, expressions[i + 1].wrapped));
                    } else {
                        tempComparisons.add(new LtOp(expressions[i].wrapped, expressions[i + 1].wrapped));
                    }
                }

                if ((expressions[i] == null || expressions[i + 1] == null) && !tempComparisons.isEmpty()) {
                    if (tempComparisons.size() == 1) {
                        secondary.addAll(tempComparisons);
                    } else {
                        compounds.add(new CompoundComparison(tempComparisons.toArray(new BinaryComparison[0])));
                    }
                    tempComparisons.clear();
                }
            }
            if (!tempComparisons.isEmpty()) {
                if (tempComparisons.size() == 1) {
                    secondary.addAll(tempComparisons);
                } else {
                    compounds.add(new CompoundComparison(tempComparisons.toArray(new BinaryComparison[0])));
                }
                tempComparisons.clear();
            }

            if (compounds.size() == 1 && secondary.isEmpty()) {
                return compounds.getFirst();
            }
            Expression primaryAndOp = null, secondaryAndOp = null;
            if (!compounds.isEmpty()) {
                primaryAndOp = BinaryExpression.fromManyOperands(compounds.toArray(new Expression[0]), 0, ShortCircuitAndOp.class);
            }
            if (!secondary.isEmpty()) {
                secondaryAndOp = BinaryExpression.fromManyOperands(secondary.toArray(new Expression[0]), 0, ShortCircuitAndOp.class);
            }
            if (secondaryAndOp != null && primaryAndOp != null) {
                return new ShortCircuitAndOp(primaryAndOp, secondaryAndOp);
            }
            return secondaryAndOp == null ? primaryAndOp : secondaryAndOp;
        }
        return expressionNode;
    }

    private int _findSameExpression(Expression expr, TaggedBinaryComparisonOperand[] weighted) {
        for (int i = 0; i < weighted.length; i++) {
            if (weighted[i] != null && expr.equals(weighted[i].wrapped)) {
                return i;
            }
        }
        return -1;
    }

    private void _collectCompoundStatementElements(Expression node, List<BinaryComparison> primary, List<Expression> secondary) {
        // Точка входа в эту функцию всегда ShortCircuitOp, именно с нее начинается сбор всех частей составного сравнения
        if (node instanceof BinaryComparison op) {
            if (op instanceof NotEqOp || op instanceof EqOp) {
                secondary.add(op);
            } else {
                primary.add(op);
            }
        } else if (node instanceof ShortCircuitAndOp op) {
            _collectCompoundStatementElements(op.getLeft(), primary, secondary);
            _collectCompoundStatementElements(op.getRight(), primary, secondary);
        } else {
            secondary.add(node);
        }
    }

    private String comparisonToString(BinaryComparison node) {
        String pattern = "";
        if (node instanceof EqOp) {
            pattern = "%s == %s";
        } else if (node instanceof NotEqOp) {
            pattern = "%s != %s";
        } else if (node instanceof GeOp) {
            pattern = "%s >= %s";
        } else if (node instanceof LeOp) {
            pattern = "%s <= %s";
        } else if (node instanceof GtOp) {
            pattern = "%s > %s";
        } else if (node instanceof LtOp) {
            pattern = "%s < %s";
        }
        return String.format(pattern, toString(node.getLeft()), toString(node.getRight()));
    }

    private String compoundComparisonToString(CompoundComparison node) {
        StringBuilder sb = new StringBuilder();
        sb.append(toString(node.getComparisons().getFirst().getLeft()));
        for (BinaryComparison cmp : node.getComparisons()) {
            if (cmp instanceof EqOp) {
                sb.append(" == ");
            } else if (cmp instanceof NotEqOp) {
                sb.append(" != ");
            } else if (cmp instanceof GeOp) {
                sb.append(" >= ");
            } else if (cmp instanceof LeOp) {
                sb.append(" <= ");
            } else if (cmp instanceof GtOp) {
                sb.append(" > ");
            } else if (cmp instanceof LtOp) {
                sb.append(" < ");
            }
            sb.append(toString(cmp.getRight()));
        }
        return sb.toString();
    }

    // END COMPARISONS SECTION //
}