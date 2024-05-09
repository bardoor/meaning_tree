package org.vstu.meaningtree.languages.parsers;

import org.treesitter.*;
import org.vstu.meaningtree.MeaningTree;
import org.vstu.meaningtree.nodes.*;
import org.vstu.meaningtree.nodes.bitwise.*;
import org.vstu.meaningtree.nodes.comparison.*;
import org.vstu.meaningtree.nodes.declarations.*;
import org.vstu.meaningtree.nodes.definitions.ClassDefinition;
import org.vstu.meaningtree.nodes.definitions.DefinitionArgument;
import org.vstu.meaningtree.nodes.definitions.FunctionDefinition;
import org.vstu.meaningtree.nodes.identifiers.ScopedIdentifier;
import org.vstu.meaningtree.nodes.identifiers.SimpleIdentifier;
import org.vstu.meaningtree.nodes.literals.*;
import org.vstu.meaningtree.nodes.logical.NotOp;
import org.vstu.meaningtree.nodes.logical.ShortCircuitAndOp;
import org.vstu.meaningtree.nodes.logical.ShortCircuitOrOp;
import org.vstu.meaningtree.nodes.math.*;
import org.vstu.meaningtree.nodes.statements.*;
import org.vstu.meaningtree.nodes.types.*;
import org.vstu.meaningtree.nodes.unary.UnaryMinusOp;
import org.vstu.meaningtree.nodes.unary.UnaryPlusOp;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class PythonLanguage extends Language {

    @Override
    public MeaningTree getMeaningTree(String code) {
        _code = code;
        TSParser parser = new TSParser();
        TSLanguage javaLanguage = new TreeSitterPython();

        parser.setLanguage(javaLanguage);
        TSTree tree = parser.parseString(null, code);
        try {
            tree.printDotGraphs(new File("TSTree.dot"));
        } catch (IOException e) { }

        return new MeaningTree(fromTSNode(tree.getRootNode()));
    }

    private Node fromTSNode(TSNode node) {
        if (node.isNull()) {
            return null;
        }
        String nodeType = node.getType();
        return switch (nodeType) {
            case "module" -> createEntryPoint(node);
            case "block" -> fromCompoundTSNode(node);
            case "if_statement" -> fromIfStatementTSNode(node);
            case "expression_statement" -> fromExpressionStatementTSNode(node);
            case "parenthesized_expression" -> fromParenthesizedExpressionTSNode(node);
            case "binary_operator" -> fromBinaryExpressionTSNode(node);
            case "unary_expression" -> fromUnaryExpressionTSNode(node);
            case "not_operator" -> fromNotOperatorTSNode(node);
            case "pass_statement" -> null;
            case "integer" -> fromIntegerLiteralTSNode(node);
            case "float" -> fromFloatLiteralTSNode(node);
            case "identifier" -> fromIdentifier(node);
            case "keyword_argument" -> fromDefinitionArgument(node);
            case "comparison_operator" -> fromComparisonTSNode(node);
            case "list", "set", "tuple" -> fromList(node, node.getType());
            case "dictionary" -> fromDictionary(node);
            case "string" -> fromString(node);
            case "slice" -> fromSlice(node);
            case "for_statement" -> fromForLoop(node);
            case "class_definition" -> fromClass(node);
            case "comment" -> fromComment(node);
            case "boolean_operator" -> fromBooleanOperatorTSNode(node);
            case "none" -> new NullLiteral();
            case "type" -> determineType(node);
            case "call" -> fromFunctionCall(node);
            case "wildcard_import" -> ScopedIdentifier.ALL;
            case "break_statement" -> new BreakStatement();
            case "continue_statement" -> new ContinueStatement();
            case "subscript" -> fromIndexTSNode(node);
            case "dotted_name" -> fromDottedNameTSNode(node);
            case "import_statement", "import_from_statement" -> fromImportNodes(node);
            case "attribute"-> fromAttributeTSNode(node);
            case "return_statement" -> fromReturnTSNode(node);
            case "conditional_expresstion"-> fromTernaryOperatorTSNode(node);
            case "assignment", "named_expression" -> fromAssignmentTSNode(node);
            case "function_definition", "decorated_definition" -> fromFunctionTSNode(node);
            case null, default -> throw new UnsupportedOperationException(String.format("Can't parse %s", node.getType()));
        };
    }

    private DefinitionArgument fromDefinitionArgument(TSNode node) {
        SimpleIdentifier ident = (SimpleIdentifier) fromTSNode(node.getChildByFieldName("name"));
        Expression expr = (Expression) fromTSNode(node.getChildByFieldName("value"));
        return new DefinitionArgument(ident, expr);
    }

    private Import fromImportNodes(TSNode node) {
        Identifier scope = null;
        if (!node.getChildByFieldName("module_name").isNull()) {
            scope = (Identifier) fromTSNode(node.getChildByFieldName("module_name"));
        }
        TSNode moduleNode = node.getChildByFieldName("name");
        Identifier alias = null, member = null;
        if (moduleNode.getType().equals("aliased_import")) {
            alias = (Identifier) fromTSNode(moduleNode.getChildByFieldName("alias"));
            member = (Identifier) fromTSNode(moduleNode.getChildByFieldName("name"));
        } else {
            member = (Identifier) fromTSNode(moduleNode);
        }
        return new Import(Import.ImportType.LIBRARY, scope, alias, member);
    }

    private FunctionCall fromFunctionCall(TSNode node) {
        Node ident = fromTSNode(node.getChildByFieldName("function"));
        if (ident instanceof MemberAccess memAccess) {
            ident = memAccess.toScopedIdentifier();
        }
        List<Expression> exprs = new ArrayList<>();
        TSNode arguments = node.getChildByFieldName("arguments");
        for (int i = 0; i < arguments.getNamedChildCount(); i++) {
            exprs.add((Expression) fromTSNode(arguments.getNamedChild(i)));
        }
        return new FunctionCall((Identifier) ident, exprs.toArray(new Expression[0]));
    }

    private Annotation fromDecorator(TSNode node) {
        TSNode child = node.getNamedChild(0);
        if (child.getType().equals("call")) {
            FunctionCall call = fromFunctionCall(node);
            return new Annotation(call.getFunctionName(), call.getArguments().toArray(new Expression[0]));
        } else {
            Node ident = fromTSNode(node.getNamedChild(0));
            if (ident instanceof MemberAccess memAccess) {
                ident = memAccess.toScopedIdentifier();
            }
            return new Annotation((Identifier) ident);
        }
    }

    private Node fromFunctionTSNode(TSNode node) {
        Annotation anno = null;
        if (node.getType().equals("decorated_definition")) {
            anno = fromDecorator(node.getChildByFieldName("decorator"));
            node = node.getChildByFieldName("definition");
        }
        SimpleIdentifier name = new SimpleIdentifier(getCodePiece(node.getChildByFieldName("name")));
        List<DeclarationArgument> arguments = new ArrayList<>();
        for (int i = 0; i < node.getChildByFieldName("parameters").getNamedChildCount(); i++) {
            arguments.add(fromDeclarationArgument(node.getChildByFieldName("parameters").getNamedChild(i)));
        }
        Type returnType = determineType(node.getChildByFieldName("return_type"));
        Statement body = (Statement) fromTSNode(node.getChildByFieldName("body"));
        return new FunctionDefinition(new FunctionDeclaration(name, returnType, anno, arguments.toArray(new DeclarationArgument[0])), body);
    }

    private DeclarationArgument fromDeclarationArgument(TSNode namedChild) {
        Type type = null;
        Expression initial = null;
        boolean isListUnpacking = false;
        if (namedChild.getType().equals("typed_parameter")) {
            type = determineType(namedChild.getChildByFieldName("type"));
        }  else if (namedChild.getType().equals("typed_default_parameter")) {
            type = determineType(namedChild.getChildByFieldName("type"));
            initial = (Expression) fromTSNode(namedChild.getChildByFieldName("value"));
        } else if (namedChild.getType().equals("default_parameter")) {
            initial = (Expression) fromTSNode(namedChild.getChildByFieldName("value"));
        } else if (namedChild.getType().equals("list_splat_pattern")) {
            isListUnpacking = true;
        }
        SimpleIdentifier identifier = (SimpleIdentifier) fromTSNode(namedChild.getNamedChild(0));
        return new DeclarationArgument(type, isListUnpacking, identifier, initial);
    }

    private ClassDefinition fromClass(TSNode node) {
        ClassDeclaration classDecl = new ClassDeclaration((SimpleIdentifier) fromTSNode(node));
        Type type = new UserType(classDecl.getName());
        CompoundStatement body = (CompoundStatement) fromTSNode(node.getChildByFieldName("body"));
        List<Node> nodes = new ArrayList<>();
        for (Node bodyNode : body) {
            if (bodyNode instanceof VariableDeclaration var) {
                nodes.add(var.makeField(VisibilityModifier.PUBLIC, false));
            } else if (bodyNode instanceof FunctionDefinition func) {
                boolean isStatic = false;
                Annotation anno = ((FunctionDeclaration) (func.getDeclaration())).getAnnotation();
                if (anno != null) {
                    isStatic = anno.getName().toString().equals("staticmethod") || anno.getName().toString().equals("classmethod");
                }
                nodes.add(func.makeMethod(type, isStatic, VisibilityModifier.PUBLIC));
            } else {
                nodes.add(bodyNode);
            }
        }
        return new ClassDefinition(classDecl, nodes);
    }

    private Statement fromForLoop(TSNode node) {
        SimpleIdentifier left = (SimpleIdentifier) fromTSNode(node.getChildByFieldName("left"));
        Node right = fromTSNode(node.getChildByFieldName("right"));
        Statement body = (Statement) fromTSNode(node.getChildByFieldName("body"));
        if (right instanceof FunctionCall call) {
            String funcName = call.getFunctionName().toString();
            if (funcName.equals("range")) {
                List<Expression> args = call.getArguments();
                Expression start = null, stop = null, step = null;
                switch (args.size()) {
                    case 0:
                        throw new IllegalArgumentException("Range requires at least 1 argument");
                    case 1:
                        stop = args.getFirst();
                        break;
                    case 2:
                        start = args.getFirst();
                        stop = args.get(1);
                        break;
                    default:
                        start = args.getFirst();
                        stop = args.get(1);
                        step = args.get(2);
                        break;
                }
                return new RangeForLoop(start, stop, step, left, body);
            }
            return new ForEachLoop(new VariableDeclaration(null, left), (Expression) right, body);
        } else {
            return new ForEachLoop(new VariableDeclaration(null, left), (Expression) right, body);
        }
    }

    private WhileLoop fromWhileLoop(TSNode node) {
        Expression condition = (Expression) fromTSNode(node.getChildByFieldName("condition"));
        Statement body = (Statement)  fromTSNode(node.getChildByFieldName("body"));
        return new WhileLoop(condition, body);
    }

    private IndexExpression fromIndexTSNode(TSNode node) {
        Expression base = (Expression) fromTSNode(node.getChildByFieldName("value"));
        Expression index = (Expression) fromTSNode(node.getChildByFieldName("subscript"));
        return new IndexExpression(base, index);
    }

    private MemberAccess fromAttributeTSNode(TSNode node) {
        Expression expr = (Expression) fromTSNode(node.getChildByFieldName("object"));
        SimpleIdentifier member = (SimpleIdentifier) fromTSNode(node.getChildByFieldName("attribute"));
        return new MemberAccess(expr, member);
    }

    private DictionaryLiteral fromDictionary(TSNode node) {
        SortedMap<Expression, Expression> dict = new TreeMap<>();
        for (int i = 0; i < node.getNamedChildCount(); i++) {
            Expression key = (Expression) fromTSNode(node.getChild(i).getChildByFieldName("key"));
            Expression value = (Expression) fromTSNode(node.getChild(i).getChildByFieldName("value"));
            dict.put(key, value);
        }
        return new DictionaryLiteral(dict);
    }

    private TernaryOperator fromTernaryOperatorTSNode(TSNode node) {
        Expression thenExpr = (Expression) fromTSNode(node.getNamedChild(0));
        Expression ifCond = (Expression) fromTSNode(node.getNamedChild(1));
        Expression elseExpr = (Expression) fromTSNode(node.getNamedChild(2));
        return new TernaryOperator(ifCond, thenExpr, elseExpr);
    }

    private ScopedIdentifier fromDottedNameTSNode(TSNode node) {
        List<SimpleIdentifier> members = new ArrayList<>();
        for (int i = 0; i < node.getNamedChildCount(); i++) {
            members.add((SimpleIdentifier) fromTSNode(node.getNamedChild(i)));
        }
        return new ScopedIdentifier(members.toArray(new SimpleIdentifier[0]));
    }

    private Range fromSlice(TSNode node) {
        Expression start = null;
        Expression stop = null;
        Expression step = null;

        int stage = 0;
        for (int i = 0; i < node.getChildCount(); i++) {
            if (node.getChild(i).getType().equals(":")) {
                stage += 1;
            } else {
                switch (stage) {
                    case 0:
                        start = (Expression) fromTSNode(node.getChild(i));
                        break;
                    case 1:
                        stop = (Expression) fromTSNode(node.getChild(i));
                        break;
                    case 2:
                        step = (Expression) fromTSNode(node.getChild(i));
                        break;
                }
            }
        }
        return new Range(start, stop, step);
    }

    private ReturnStatement fromReturnTSNode(TSNode node) {
        if (node.getNamedChildCount() > 0) {
            return new ReturnStatement((Expression) fromTSNode(node.getNamedChild(0)));
        }
        return new ReturnStatement(null);
    }

    private Node createEntryPoint(TSNode node) {
        // detect if __name__ == __main__ construction
        CompoundStatement compound = fromCompoundTSNode(node);
        String functionName = null;
        for (Node programNode : compound) {
            if (programNode instanceof IfStatement ifStmt) {
                ConditionBranch mainBranch = ifStmt.getBranches().get(0);
                if (mainBranch.getCondition() instanceof EqOp eqOp) {
                    if (eqOp.getLeft().toString().equals("__name__") && eqOp.getRight().toString().equals("__main__")) {
                        CompoundStatement body = (CompoundStatement) mainBranch.getBody();
                        if (body.getLength() == 1 && body.getNodes()[0] instanceof FunctionCall call) {
                            if (call.getFunctionName() instanceof SimpleIdentifier) {
                                functionName = call.getFunctionName().toString();
                            }
                        }
                    }
                    //TODO: add various main condition body support
                }
            }
        }
        FunctionDefinition mainFunction = null;
        for (Node programNode : compound) {
            if (programNode instanceof FunctionDefinition func && func.getName().toString().equals(functionName)) {
                mainFunction = func;
            }
        }
        return new ProgramEntryPoint(List.of(compound.getNodes()), mainFunction);
    }

    private Identifier fromIdentifier(TSNode node) {
        return new SimpleIdentifier(getCodePiece(node));
    }

    private Node fromString(TSNode node) {
        TSNode content = node.getChildByFieldName("string_content");
        //TODO: hardcode output in viewer? What about escaping and ", ', """
        if (node.getChildByFieldName("string_start").getType().equals("\"\"\"")
                && node.getParent().getType().equals("expression_statement")) {
            return new Comment(getCodePiece(content));
        }
        return new StringLiteral(getCodePiece(content));
    }

    private Comment fromComment(TSNode node) {
        return new Comment(getCodePiece(node).replace("#", "").trim());
    }

    private Type determineType(TSNode typeNode) {
        if (typeNode.getNamedChildCount() > 0 && typeNode.getNamedChild(0).getType().equals("generic_type")) {
            TSNode genericTypeNode = typeNode.getNamedChild(0);
            List<Type> genericTypes = new ArrayList<>();
            String typeName = getCodePiece(typeNode.getNamedChild(0));
            for (int i = 0; i < genericTypeNode.getNamedChildCount(); i++) {
                genericTypes.add(determineType(genericTypeNode.getNamedChild(i)));
            }
            switch (typeName) {
                case "list":
                    return new ListType(genericTypes.getFirst());
                case "tuple":
                    return new UnmodifiableListType(genericTypes.getFirst());
                case "dict":
                    return new DictionaryType(genericTypes.getFirst(), genericTypes.get(1));
                default:
                    return new GenericUserType(new SimpleIdentifier(typeName), genericTypes.toArray(new Type[0]));
            }
        }
        switch (getCodePiece(typeNode)) {
            case "str":
                return new StringType();
            case "int":
                return new IntType();
            case "float":
                return new FloatType();
            case "list":
                return new ListType(new UnknownType());
            case "tuple":
                return new UnmodifiableListType(new UnknownType());
            case "dict":
                return new DictionaryType(new UnknownType(), new UnknownType());
            case "set":
                return new SetType(new UnknownType());
            default:
                return new UserType(new SimpleIdentifier(getCodePiece(typeNode)));
        }
    }

    private Node fromAssignmentTSNode(TSNode node) {
        TSNode operator = node.getChildByFieldName("left").getNextSibling();
        Expression left = (Expression) fromTSNode(node.getChildByFieldName("left"));
        Expression right = (Expression) fromTSNode(node.getChildByFieldName("right"));

        if (!node.getChildByFieldName("type").isNull()) {
            Type type = determineType(node.getChildByFieldName("type"));
            return new VariableDeclaration(type, (SimpleIdentifier) left, right);
        }

        if (getCodePiece(operator).equals("=")) {
            return new AssignmentStatement(left, right);
        } else if (getCodePiece(operator).equals(":=")){
            return new AssignmentExpression(left, right);
        } else {
            AugmentedAssignmentOperator augOp;
            switch (getCodePiece(operator)) {
                case "+=":
                    augOp = AugmentedAssignmentOperator.ADD;
                    break;
                case "-=":
                    augOp = AugmentedAssignmentOperator.SUB;
                    break;
                case "*=":
                    augOp = AugmentedAssignmentOperator.MUL;
                    break;
                case "/=":
                    augOp = AugmentedAssignmentOperator.DIV;
                    break;
                case "//=":
                    augOp = AugmentedAssignmentOperator.FLOORDIV;
                    break;
                case "|=":
                    augOp = AugmentedAssignmentOperator.BITWISE_OR;
                    break;
                case "&=":
                    augOp = AugmentedAssignmentOperator.BITWISE_AND;
                    break;
                case ">>=":
                    augOp = AugmentedAssignmentOperator.BITWISE_SHIFT_RIGHT;
                    break;
                case "<<=":
                    augOp = AugmentedAssignmentOperator.BITWISE_SHIFT_LEFT;
                    break;
                case "%=":
                    augOp = AugmentedAssignmentOperator.MOD;
                    break;
                case "**=":
                    augOp = AugmentedAssignmentOperator.POW;
                    break;
                case "^=":
                    augOp = AugmentedAssignmentOperator.BITWISE_XOR;
                    break;
                default:
                    augOp = AugmentedAssignmentOperator.NONE;
            }
            return new AssignmentStatement(left, right, augOp);
        }
    }

    private Node fromList(TSNode node, String type) {
        List<Expression> exprs = new ArrayList<>();
        for (int i = 0; i < node.getNamedChildCount(); i++) {
            Expression expr = (Expression) fromTSNode(node.getChild(i));
            exprs.add(expr);
        }
        return switch (type) {
            case "list" -> new ListLiteral(exprs.toArray(new Expression[0]));
            case "tuple" -> new UnmodifiableListLiteral(exprs.toArray(new Expression[0]));
            case "set" -> new SetLiteral(exprs.toArray(new Expression[0]));
            default -> null;
        };
    }

    private CompoundStatement fromCompoundTSNode(TSNode node) {
        ArrayList<Node> nodes = new ArrayList<>();
        for (int i = 0; i < node.getChildCount(); i++) {
            nodes.add(fromTSNode(node.getChild(i)));
        }
        return new CompoundStatement(nodes.toArray(new Node[0]));
    }

    private IfStatement fromIfStatementTSNode(TSNode node) {
        List<ConditionBranch> branches = new ArrayList<>();
        branches.add(createConditionBranchTSNode(node));
        Statement elseBranch = null;
        TSNode altNode = node.getChildByFieldName("alternative");
        while (!altNode.isNull() &&
                (altNode.getType().equals("elif_clause") || altNode.getType().equals("else_clause"))) {
            if (altNode.getType().equals("elif_clause")) {
                branches.add(createConditionBranchTSNode(altNode));
            } else {
                elseBranch = (Statement) fromTSNode(altNode.getChildByFieldName("block"));
            }
            altNode = altNode.getNextNamedSibling();
        }
        return new IfStatement(branches, elseBranch);
    }

    private ConditionBranch createConditionBranchTSNode(TSNode node) {
        Expression condition = (Expression) fromTSNode(node.getChildByFieldName("condition"));
        CompoundStatement consequence = (CompoundStatement) fromTSNode(node.getChildByFieldName("consequence"));
        return new ConditionBranch(condition, consequence);
    }

    private UnaryExpression fromUnaryExpressionTSNode(TSNode node) {
        Expression argument = (Expression) fromTSNode(node.getChildByFieldName("operand"));
        TSNode operation = node.getChildByFieldName("operator");
        return switch (getCodePiece(operation)) {
            case "~" -> new InversionOp(argument);
            case "-" -> new UnaryMinusOp(argument);
            case "+" -> new UnaryPlusOp(argument);
            case null, default -> throw new UnsupportedOperationException();
        };
    }

    private NotOp fromNotOperatorTSNode(TSNode node) {
        Expression argument = (Expression) fromTSNode(node.getChildByFieldName("argument"));
        return new NotOp(argument);
    }


    private Node fromExpressionStatementTSNode(TSNode node) {
        return fromTSNode(node.getChild(0));
    }

    private ParenthesizedExpression fromParenthesizedExpressionTSNode(TSNode node) {
        return new ParenthesizedExpression((Expression) fromTSNode(node.getChild(1)));
    }

    private IntegerLiteral fromIntegerLiteralTSNode(TSNode node) {
        String value = getCodePiece(node);
        return new IntegerLiteral(value);
    }

    private FloatLiteral fromFloatLiteralTSNode(TSNode node) {
        String value = getCodePiece(node);
        return new FloatLiteral(value);
    }

    private Node fromComparisonTSNode(TSNode node) {
        Class<? extends BinaryComparison> operator = null;
        ArrayDeque<TSNode> operands = new ArrayDeque<>();

        List<BinaryComparison> comparisons = new ArrayList<>();

        for (int i = 0; i < node.getChildCount(); i++) {
            if (operands.size() == 2) {
                Expression firstOp = (Expression) fromTSNode(operands.removeFirst());
                TSNode secondNode = operands.removeFirst();
                Expression secondOp = (Expression) fromTSNode(secondNode);
                try {
                    BinaryComparison object = operator.getDeclaredConstructor(Expression.class, Expression.class).newInstance(firstOp, secondOp);
                    comparisons.add(object);
                } catch (InstantiationException | InvocationTargetException | IllegalAccessException |
                         NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
                operands.add(secondNode);
            }
            TSNode children = node.getChild(i);

            switch (children.getType()) {
                case "<":
                    operator = LtOp.class;
                    break;
                case ">":
                    operator = GtOp.class;
                    break;
                case "<=":
                    operator = LeOp.class;
                    break;
                case ">=":
                    operator = GeOp.class;
                    break;
                case "==":
                    operator = EqOp.class;
                    break;
                case "!=":
                    operator = NotEqOp.class;
                    break;
                default:
                    operands.add(children);
            }
        }
        if (operands.size() == 2) {
            Expression firstOp = (Expression) fromTSNode(operands.removeFirst());
            TSNode secondNode = operands.removeFirst();
            Expression secondOp = (Expression) fromTSNode(secondNode);
            try {
                BinaryComparison object = operator.getDeclaredConstructor(Expression.class, Expression.class).newInstance(firstOp, secondOp);
                comparisons.add(object);
            } catch (InstantiationException | InvocationTargetException | IllegalAccessException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        return new CompoundComparison(comparisons.toArray(new BinaryComparison[0]));
    }

    private BinaryExpression fromBooleanOperatorTSNode(TSNode node) {
        Expression left = (Expression) fromTSNode(node.getChildByFieldName("left"));
        Expression right = (Expression) fromTSNode(node.getChildByFieldName("right"));
        TSNode operator = node.getChildByFieldName("operator");

        if (getCodePiece(operator).equals("and")) {
            return new ShortCircuitAndOp(left, right);
        } else if (getCodePiece(operator).equals("or")) {
            return new ShortCircuitOrOp(left, right);
        }
        return null;
    }

    private BinaryExpression fromBinaryExpressionTSNode(TSNode node) {
        Expression left = (Expression) fromTSNode(node.getChildByFieldName("left"));
        Expression right = (Expression) fromTSNode(node.getChildByFieldName("right"));
        TSNode operator = node.getChildByFieldName("operator");

        return switch (getCodePiece(operator)) {
            case "+" -> new AddOp(left, right);
            case "-" -> new SubOp(left, right);
            case "*" -> new MulOp(left, right);
            case "**" -> new PowOp(left, right);
            case "/" -> new DivOp(left, right);
            case "//" -> new FloorDivOp(left, right);
            case "%" -> new ModOp(left, right);
            case "<<" -> new LeftShiftOp(left, right);
            case ">>" -> new RightShiftOp(left, right);
            case "&" -> new BitwiseAndOp(left, right);
            case "|" -> new BitwiseOrOp(left, right);
            case "^" -> new XorOp(left, right);
            default -> throw new UnsupportedOperationException(String.format("Can't parse operator %s", getCodePiece(operator)));
        };
    }
}