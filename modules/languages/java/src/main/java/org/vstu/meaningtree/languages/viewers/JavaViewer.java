package org.vstu.meaningtree.languages.viewers;

import org.vstu.meaningtree.nodes.*;
import org.vstu.meaningtree.nodes.bitwise.*;
import org.vstu.meaningtree.nodes.declarations.*;
import org.vstu.meaningtree.nodes.definitions.ClassDefinition;
import org.vstu.meaningtree.nodes.definitions.MethodDefinition;
import org.vstu.meaningtree.nodes.definitions.ObjectConstructor;
import org.vstu.meaningtree.nodes.identifiers.Identifier;
import org.vstu.meaningtree.nodes.identifiers.ScopedIdentifier;
import org.vstu.meaningtree.nodes.identifiers.SimpleIdentifier;
import org.vstu.meaningtree.nodes.comparison.*;
import org.vstu.meaningtree.nodes.literals.*;
import org.vstu.meaningtree.nodes.logical.NotOp;
import org.vstu.meaningtree.nodes.logical.ShortCircuitAndOp;
import org.vstu.meaningtree.nodes.logical.ShortCircuitOrOp;
import org.vstu.meaningtree.nodes.math.*;
import org.vstu.meaningtree.nodes.modules.*;
import org.vstu.meaningtree.nodes.statements.*;
import org.vstu.meaningtree.nodes.types.*;
import org.vstu.meaningtree.nodes.unary.PostfixDecrementOp;
import org.vstu.meaningtree.nodes.unary.PostfixIncrementOp;
import org.vstu.meaningtree.nodes.unary.PrefixDecrementOp;
import org.vstu.meaningtree.nodes.unary.PrefixIncrementOp;

import javax.swing.plaf.nimbus.State;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.vstu.meaningtree.nodes.AugmentedAssignmentOperator.POW;

public class JavaViewer extends Viewer {

    private final String _indentation;
    private int _indentLevel;
    private final boolean _openBracketOnSameLine;
    private final boolean _bracketsAroundCaseBranches;

    public JavaViewer(int indentSpaceCount, boolean openBracketOnSameLine, boolean bracketsAroundCaseBranches) {
        _indentation = " ".repeat(indentSpaceCount);
        _indentLevel = 0;
        _openBracketOnSameLine = openBracketOnSameLine;
        _bracketsAroundCaseBranches = bracketsAroundCaseBranches;
    }

    public JavaViewer() { this(4, true, false); }

    @Override
    public String toString(Node node) {
        Objects.requireNonNull(node);
        return switch (node) {
            case FloatLiteral l -> toString(l);
            case IntegerLiteral l -> toString(l);
            case StringLiteral l -> toString(l);
            case AddOp op -> toString(op);
            case SubOp op -> toString(op);
            case MulOp op -> toString(op);
            case DivOp op -> toString(op);
            case ModOp op -> toString(op);
            case FloorDivOp op -> toString(op);
            case EqOp op -> toString(op);
            case GeOp op -> toString(op);
            case GtOp op -> toString(op);
            case LeOp op -> toString(op);
            case LtOp op -> toString(op);
            case NotEqOp op -> toString(op);
            case ShortCircuitAndOp op -> toString(op);
            case ShortCircuitOrOp op -> toString(op);
            case NotOp op -> toString(op);
            case ParenthesizedExpression expr -> toString(expr);
            case AssignmentExpression expr -> toString(expr);
            case AssignmentStatement stmt -> toString(stmt);
            case FieldDeclaration decl -> toString(decl);
            case VariableDeclaration stmt -> toString(stmt);
            case CompoundStatement stmt -> toString(stmt);
            case ExpressionStatement stmt -> toString(stmt);
            case SimpleIdentifier expr -> toString(expr);
            case IfStatement stmt -> toString(stmt);
            case GeneralForLoop stmt -> toString(stmt);
            case CompoundComparison cmp -> toString(cmp);
            case RangeForLoop rangeLoop -> toString(rangeLoop);
            case ProgramEntryPoint entryPoint -> toString(entryPoint);
            case MethodCall methodCall -> toString(methodCall);
            case FunctionCall funcCall -> toString(funcCall);
            case WhileLoop whileLoop -> toString(whileLoop);
            case ScopedIdentifier scopedIdent -> toString(scopedIdent);
            case PostfixIncrementOp inc -> toString(inc);
            case PostfixDecrementOp dec -> toString(dec);
            case PrefixIncrementOp inc -> toString(inc);
            case PrefixDecrementOp dec -> toString(dec);
            case PowOp op -> toString(op);
            case PackageDeclaration decl -> toString(decl);
            case ClassDeclaration decl -> toString(decl);
            case ClassDefinition def -> toString(def);
            case Comment comment -> toString(comment);
            case BreakStatement stmt -> toString(stmt);
            case ContinueStatement stmt -> toString(stmt);
            case ObjectConstructor objectConstructor -> toString(objectConstructor);
            case MethodDefinition methodDefinition -> toString(methodDefinition);
            case SwitchStatement switchStatement -> toString(switchStatement);
            case NullLiteral nullLiteral -> toString(nullLiteral);
            case StaticImportAll staticImportAll -> toString(staticImportAll);
            case StaticImportMembers staticImportMembers -> toString(staticImportMembers);
            case ImportAll importAll -> toString(importAll);
            case ImportMembers importMembers -> toString(importMembers);
            case UserType userType -> toString(userType);
            case ObjectNewExpression objectNewExpression -> toString(objectNewExpression);
            case BoolLiteral boolLiteral -> toString(boolLiteral);
            case MemberAccess memberAccess -> toString(memberAccess);
            case ArrayNewExpression arrayNewExpression -> toString(arrayNewExpression);
            case ArrayInitializer arrayInitializer -> toString(arrayInitializer);
            case ReturnStatement returnStatement -> toString(returnStatement);
            case CastTypeExpression castTypeExpression -> toString(castTypeExpression);
            case IndexExpression indexExpression -> toString(indexExpression);
            case TernaryOperator ternaryOperator -> toString(ternaryOperator);
            case BitwiseAndOp bitwiseAndOp -> toString(bitwiseAndOp);
            case BitwiseOrOp bitwiseOrOp -> toString(bitwiseOrOp);
            case XorOp xorOp -> toString(xorOp);
            case InversionOp inversionOp -> toString(inversionOp);
            case LeftShiftOp leftShiftOp -> toString(leftShiftOp);
            case RightShiftOp rightShiftOp -> toString(rightShiftOp);
            case MultipleAssignmentStatement multipleAssignmentStatement -> toString(multipleAssignmentStatement);
            default -> throw new IllegalStateException(String.format("Can't stringify node %s", node.getClass()));
        };
    }

    private String toString(ObjectConstructor objectConstructor) {
        MethodDeclaration constructorDeclaration =
                (MethodDeclaration) objectConstructor.getDeclaration();

        StringBuilder builder = new StringBuilder();

        String modifiers = toString(constructorDeclaration.getModifiers());
        if (!modifiers.isEmpty()) {
            builder.append(modifiers).append(" ");
        }

        String name = toString(objectConstructor.getName());
        builder.append(name);

        String parameters = toStringParameters(constructorDeclaration.getArguments());
        if (!parameters.isEmpty()) {
            builder.append(parameters);
        }

        String body = toString(objectConstructor.getBody());
        builder.append(" ").append(body);

        return builder.toString();
    }

    private String toString(MultipleAssignmentStatement multipleAssignmentStatement) {
        StringBuilder builder = new StringBuilder();

        for (AssignmentStatement stmt : multipleAssignmentStatement.getStatements()) {
            builder.append(toString(stmt)).append("\n");
        }

        return builder.toString();
    }

    private String toString(RightShiftOp rightShiftOp) {
        return toString(rightShiftOp, ">>");
    }

    private String toString(LeftShiftOp leftShiftOp) {
        return toString(leftShiftOp, "<<");
    }

    private String toString(InversionOp inversionOp) {
        return "~" + toString(inversionOp.getArgument());
    }

    private String toString(XorOp xorOp) {
        return toString(xorOp, "^");
    }

    private String toString(BitwiseOrOp bitwiseOrOp) {
        return toString(bitwiseOrOp, "|");
    }

    private String toString(BitwiseAndOp bitwiseAndOp) {
        return toString(bitwiseAndOp, "&");
    }

    private String toString(TernaryOperator ternaryOperator) {
        String condition = toString(ternaryOperator.getCondition());
        String consequence = toString(ternaryOperator.getThenExpr());
        String alternative = toString(ternaryOperator.getElseExpr());
        return "%s ? %s : %s".formatted(condition, consequence, alternative);
    }

    private String toString(IndexExpression indexExpression) {
        Expression arrayName = indexExpression.getExpr();

        if (!(arrayName instanceof Identifier)) {
            throw new IllegalStateException("Expected array name to be identifier");
        }

        String name = toString((Identifier) arrayName);
        String index = toString(indexExpression.getIndex());
        return "%s[%s]".formatted(name, index);
    }

    private String toString(CastTypeExpression castTypeExpression) {
        String castType = toString(castTypeExpression.getCastType());
        String value = toString(castTypeExpression.getValue());
        return "(%s) %s".formatted(castType, value);
    }

    private String toString(ReturnStatement returnStatement) {
        Optional<Expression> expression = returnStatement.getExpression();
        return expression.map(value -> "return " + toString(value) + ";").orElse("return;");
    }

    private String toString(ArrayInitializer initializer) {
        StringBuilder builder = new StringBuilder();
        builder.append("{");

        List<Expression> values = initializer.getValues();
        for (Expression value : values) {
            builder
                    .append(toString(value))
                    .append(", ");
        }

        if (builder.length() > 1) {
            // Удаляем лишние пробел и запятую
            builder.deleteCharAt(builder.length() - 1);
            builder.deleteCharAt(builder.length() - 1);
        }

        builder.append("}");
        return builder.toString();
    }

    private String toString(ArrayNewExpression arrayNewExpression) {
        StringBuilder builder = new StringBuilder();
        builder.append("new ");

        String type = toString(arrayNewExpression.getType());
        builder.append(type);

        String dimensions = toString(arrayNewExpression.getShape());
        builder.append(dimensions);

        Optional<ArrayInitializer> optionalInitializer = arrayNewExpression.getInitializer();
        if (optionalInitializer.isPresent()) {
            String initializer = toString(optionalInitializer.get());
            builder.append(" ").append(initializer);
        }

        return builder.toString();
    }

    private String toString(MemberAccess memberAccess) {
        String object = toString(memberAccess.getExpression());
        String member = toString(memberAccess.getMember());
        return "%s.%s".formatted(object, member);
    }

    private String toString(BoolLiteral boolLiteral) {
        return boolLiteral.getValue() ? "true" : "false";
    }

    private String toString(ObjectNewExpression objectNewExpression) {
        String typeName = toString(objectNewExpression.getType());

        String arguments = objectNewExpression
                .getConstructorArguments()
                .stream()
                .map(this::toString)
                .collect(Collectors.joining(", "));

        return "new %s(%s)".formatted(typeName, arguments);
    }

    private String toString(MethodCall methodCall) {
        String object = toString(methodCall.getObject());
        String methodName = toString(methodCall.getFunctionName());

        String arguments = methodCall
                .getArguments()
                .stream()
                .map(this::toString)
                .collect(Collectors.joining(", "));

        return "%s.%s(%s)".formatted(object, methodName, arguments);
    }

    private String toString(UserType userType) {
        return toString(userType.getName());
    }

    private String toString(StaticImportAll staticImportAll) {
        String importTemplate = "import static %s.*;";
        return importTemplate.formatted(toString(staticImportAll.getScope()));
    }

    private String toString(StaticImportMembers staticImportMembers) {
        StringBuilder builder = new StringBuilder();

        String importTemplate = "import static %s.%s;";
        for (Identifier member : staticImportMembers.getMembers()) {
            builder
                    .append(
                            importTemplate.formatted(
                                    toString(staticImportMembers.getScope()),
                                    toString(member)
                            )
                    )
                    .append("\n");
            ;
        }

        // Удаляем последний символ перевода строки
        builder.deleteCharAt(builder.length() - 1);

        return builder.toString();
    }

    private String toString(ImportAll importAll) {
        String importTemplate = "import %s.*;";
        return importTemplate.formatted(toString(importAll.getScope()));
    }

    private String toString(ImportMembers importMembers) {
        StringBuilder builder = new StringBuilder();

        String importTemplate = "import %s.%s;";
        for (Identifier member : importMembers.getMembers()) {
            builder
                    .append(
                        importTemplate.formatted(
                            toString(importMembers.getScope()),
                            toString(member)
                        )
                    )
                    .append("\n");
            ;
        }

        // Удаляем последний символ перевода строки
        builder.deleteCharAt(builder.length() - 1);

        return builder.toString();
    }

    private String toString(NullLiteral nullLiteral) {
        return "null";
    }

    private String toStringCaseBlock(Statement caseBlock) {
        StringBuilder builder = new StringBuilder();

        Statement caseBlockBody;
        if (caseBlock instanceof ConditionBranch conditionBranch) {
            builder.append("case ");
            builder.append(toString(conditionBranch.getCondition()));
            builder.append(":");
            caseBlockBody = conditionBranch.getBody();
        }
        else {
            builder.append("default:");
            caseBlockBody = caseBlock;
        }

        List<Node> nodesList;
        if (caseBlockBody instanceof CompoundStatement compoundStatement) {
            nodesList = Arrays.asList(compoundStatement.getNodes());
        }
        else {
            nodesList = List.of(caseBlockBody);
        }

        if (_bracketsAroundCaseBranches) {
            if (_openBracketOnSameLine) {
                builder.append(" {\n");
            }
            else {

                builder.append("\n").append(indent("{\n"));
            }
        }
        else {
            builder.append("\n");
        }

        increaseIndentLevel();
        for (Node node : nodesList) {
            builder
                    .append(indent(toString(node)))
                    .append("\n");
        }
        builder.deleteCharAt(builder.length() - 1);
        decreaseIndentLevel();

        if (_bracketsAroundCaseBranches) {
            builder
                    .append("\n")
                    .append(indent("}"));
        }

        return builder.toString();
    }

    private String toString(SwitchStatement switchStatement) {
        StringBuilder builder = new StringBuilder();

        builder.append("switch (");
        builder.append(toString(switchStatement.getTargetExpression()));
        builder.append(") ");

        if (_openBracketOnSameLine) {
            builder.append("{\n");
        }
        else {
            builder.append("\n").append(indent("{\n"));
        }

        increaseIndentLevel();
        for (ConditionBranch caseBlock : switchStatement.getCases()) {
            builder
                    .append(indent(toStringCaseBlock(caseBlock)))
                    .append("\n");
        }
        if (switchStatement.hasDefaultCase()) {
            builder
                    .append(indent(toStringCaseBlock(switchStatement.getDefaultCase())))
                    .append("\n");
        }
        decreaseIndentLevel();

        builder.append(indent("}"));
        return builder.toString();
    }

    private String toString(DeclarationArgument parameter) {
        String type = toString(parameter.getType());
        String name = toString(parameter.getName());
        return "%s %s".formatted(type, name);
    }

    // В отличие от всех остальных методов, данный называется так,
    // чтобы избежать конфликтов с другими методами:
    // toStringParameters(List<Modifier> modifiers)
    // и toStringParameters(List<DeclarationArgument> parameters)
    // с точки зрения Java один и тот же тип...
    private String toStringParameters(List<DeclarationArgument> parameters) {
        StringBuilder builder = new StringBuilder();
        builder.append("(");

        int i;
        for (i = 0; i < parameters.size(); i++) {
            DeclarationArgument parameter = parameters.get(i);
            builder.append("%s, ".formatted(toString(parameter)));
        }

        // Удаляем последний пробел и запятую, если был хотя бы один параметр
        if (i > 0) {
            builder.deleteCharAt(builder.length() - 1);
            builder.deleteCharAt(builder.length() - 1);
        }

        builder.append(")");
        return builder.toString();
    }

    private String toString(MethodDeclaration methodDeclaration) {
        String modifiersList = toString(methodDeclaration.getModifiers());
        String returnType = toString(methodDeclaration.getReturnType());
        String name = toString(methodDeclaration.getName());
        String parameters = toStringParameters(methodDeclaration.getArguments());
        return "%s %s %s%s".formatted(modifiersList, returnType, name, parameters);
    }

    private String toString(MethodDefinition methodDefinition) {
        // Преобразование типа нужно, чтобы избежать вызова toString(Node node)
        String methodDeclaration = toString((MethodDeclaration) methodDefinition.getDeclaration());
        String body = toString(methodDefinition.getBody());
        return "%s %s".formatted(methodDeclaration, body);
    }

    private String toString(ContinueStatement stmt) {
        return "continue;";
    }

    private String toString(BreakStatement stmt) {
        return "break;";
    }

    private String toString(Comment comment) {
        if (comment.isMultiline()) {
            return "/*" + comment.getUnescapedContent() + "*/";
        }

        return "//%s".formatted(comment.getUnescapedContent());
    }

    private String toString(FieldDeclaration decl) {
        StringBuilder builder = new StringBuilder();

        String modifiers = toString(decl.getModifiers());
        builder.append(modifiers);
        // Добавляем пробел в конце, если есть хотя бы один модификатор
        if (!builder.isEmpty()) {
            builder.append(" ");
        }

        VariableDeclaration variableDeclaration = new VariableDeclaration(decl.getType(), decl.getDeclarators());
        builder.append(toString(variableDeclaration));

        return builder.toString();
    }

    private String toString(List<Modifier> modifiers) {
        StringBuilder builder = new StringBuilder();

        for (Modifier modifier : modifiers) {
            builder.append(
                    switch (modifier) {
                        case PUBLIC -> "public";
                        case PRIVATE -> "private";
                        case PROTECTED -> "protected";
                        case ABSTRACT -> "abstract";
                        case CONST -> "final";
                        case STATIC -> "static";
                        default -> throw new IllegalArgumentException();
                    }
            ).append(" ");
        }

        // Удаляем в конце ненужный пробел, если было более одного модификатора
        if (!builder.isEmpty()) {
            builder.deleteCharAt(builder.length() - 1);
        }

        return builder.toString();
    }

    private String toString(ClassDeclaration decl) {
        String modifiers = toString(decl.getModifiers()) + " ";
        return modifiers + "class " + toString(decl.getName());
    }

    private String toString(ClassDefinition def) {
        return "%s %s".formatted(toString(def.getDeclaration()), toString(def.getBody()));
    }

    public String toString(FloatLiteral literal) {
        return Double.toString(literal.getDoubleValue());
    }

    public String toString(IntegerLiteral literal) {
        return Long.toString((long) literal.getValue());
    }

    public String toString(StringLiteral literal) {
        if (literal.isMultiline()) {
            return "\"\"\"%s\"\"\"".formatted(literal.getUnescapedValue());
        }

        return "\"%s\"".formatted(literal.getUnescapedValue());
    }

    private String toString(BinaryExpression expr, String sign) {
        return String.format("%s %s %s", toString(expr.getLeft()), sign, toString(expr.getRight()));
    }

    public String toString(AddOp op) {
        return toString(op, "+");
    }

    public String toString(SubOp op) {
        return toString(op, "-");
    }

    public String toString(MulOp op) {
        return toString(op, "*");
    }

    public String toString(DivOp op) {
        return toString(op, "/");
    }

    public String toString(ModOp op) {
        return toString(op, "%");
    }

    public String toString(FloorDivOp op) {
        return toString(op, "/");
    }

    public String toString(EqOp op) {
        return toString(op, "==");
    }

    public String toString(GeOp op) {
        return toString(op, ">=");
    }

    public String toString(GtOp op) {
        return toString(op, ">");
    }

    public String toString(LeOp op) {
        return toString(op, "<=");
    }

    public String toString(LtOp op) {
        return toString(op, "<");
    }

    public String toString(NotEqOp op) {
        return toString(op, "!=");
    }

    public String toString(ShortCircuitAndOp op) {
        return toString(op, "&&");
    }

    public String toString(ShortCircuitOrOp op) {
        return toString(op, "||");
    }

    public String toString(NotOp op) {
        return String.format("!%s", toString(op.getArgument()));
    }

    public String toString(ParenthesizedExpression expr) {
        return String.format("(%s)", toString(expr.getExpression()));
    }

    private String toString(AugmentedAssignmentOperator op, Expression left, Expression right) {
        String l = toString(left);
        String r = toString(right);

        // В Java нет встроенного оператора возведения в степень, следовательно,
        // нет и соотвествующего оператора присванивания, поэтому этот случай обрабатываем по особому
        // TODO: нужно убедится, что был импортирован модуль Math
        if (op == POW) {
            return "%s = Math.pow(%s, %s)".formatted(l, l, r);
        }

        String o = switch (op) {
            case NONE -> "=";
            case ADD -> "+=";
            case SUB -> "-=";
            case MUL -> "*=";
            // В Java тип деления определяется не видом операции, а типом операндов,
            // поэтому один и тот же оператор
            case DIV, FLOOR_DIV -> "/=";
            case BITWISE_AND -> "&=";
            case BITWISE_OR -> "|=";
            case BITWISE_XOR -> "^=";
            case BITWISE_SHIFT_LEFT -> "<<=";
            case BITWISE_SHIFT_RIGHT -> ">>=";
            case MOD -> "%=";
            default -> throw new IllegalStateException("Unexpected type of augmented assignment operator: " + op);
        };

        if (right instanceof IntegerLiteral integerLiteral
                && (long) integerLiteral.getValue() == 1
                && (o.equals("+=") || o.equals("-="))) {
            o = switch (o) {
                case "+=" -> "++";
                case "-=" -> "--";
                default -> throw new IllegalArgumentException();
            };

            return l + o;
        }

        return "%s %s %s".formatted(l, o, r);
    }

    public String toString(AssignmentExpression expr) {
        return toString(expr.getAugmentedOperator(), expr.getLValue(), expr.getRValue());
    }

    public String toString(AssignmentStatement stmt) {
        return "%s;".formatted(toString(stmt.getAugmentedOperator(), stmt.getLValue(), stmt.getRValue()));
    }

    private String toString (Type t) {
        return switch (t) {
            case FloatType floatType -> toString(floatType);
            case IntType intType -> toString(intType);
            case BooleanType booleanType -> toString(booleanType);
            case StringType stringType -> toString(stringType);
            case VoidType voidType -> toString(voidType);
            case UnknownType unknownType -> toString(unknownType);
            case ArrayType arrayType -> toString(arrayType);
            case UserType userType -> toString(userType);
            default -> throw new IllegalStateException("Unexpected value: " + t.getClass());
        };
    }

    private String toString(FloatType t) {
        return "double";
    }

    private String toString(IntType t) {
        return "int";
    }

    private String toString(BooleanType t) {
        return "boolean";
    }

    private String toString(StringType t) {
        return "String";
    }

    private String toString(VoidType type) {
        return "void";
    }

    private String toString(UnknownType type) {
        return "Object";
    }

    private String toString(Shape shape) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < shape.getDimensionCount(); i++) {
            builder.append("[");

            Optional<Expression> dimension = shape.getDimension(i);
            dimension.ifPresent(expression -> builder.append(toString(expression)));

            builder.append("]");
        }

        return builder.toString();
    }

    private String toString(ArrayType type) {
        StringBuilder builder = new StringBuilder();

        String baseType = toString(type.getItemType());
        builder.append(baseType);
        builder.append(toString(type.getShape()));

        return builder.toString();
    }

    private String toString(VariableDeclarator varDecl) {
        StringBuilder builder = new StringBuilder();

        String identifier = toString(varDecl.getIdentifier());
        builder.append(identifier);

        if (varDecl.hasInitialization()) {
            varDecl.getRValue().ifPresent(init ->
                    builder.append(" = ").append(toString(init))
            );
        }

        return builder.toString();
    }

    public String toString(VariableDeclaration stmt) {
        StringBuilder builder = new StringBuilder();

        String declarationType = toString(stmt.getType());
        builder.append(declarationType).append(" ");

        for (VariableDeclarator varDecl : stmt.getDeclarators()) {
            builder.append(toString(varDecl)).append(", ");
        }
        // Чтобы избежать лишней головной боли на проверки "а последняя ли это декларация",
        // я автоматически после каждой декларации добавляю запятую и пробел,
        // но для последней декларации они не нужны, поэтому эти два символа удаляются,
        // как сделать красивее - не знаю...
        builder.deleteCharAt(builder.length() - 1);
        builder.deleteCharAt(builder.length() - 1);

        builder.append(";");
        return builder.toString();
    }

    private void increaseIndentLevel() {
        _indentLevel++;
    }

    private void decreaseIndentLevel() {
        _indentLevel--;

        if (_indentLevel < 0) {
            throw new RuntimeException("Indentation level can't be less than zero");
        }
    }

    private String indent(String s) {
        if (_indentLevel == 0) {
            return s;
        }

        return _indentation.repeat(Math.max(0, _indentLevel)) + s;
    }

    public String toString(CompoundStatement stmt) {
        StringBuilder builder = new StringBuilder();
        builder.append("{\n");
        increaseIndentLevel();
        for (Node node : stmt) {
            String s = toString(node);
            if (s.isEmpty()) {
                continue;
            }

            s = indent(String.format("%s\n", s));
            builder.append(s);
        }
        decreaseIndentLevel();
        builder.append(indent("}"));
        return builder.toString();
    }

    public String toString(ExpressionStatement stmt) {
        return String.format("%s;", toString(stmt.getExpression()));
    }

    public String toString(SimpleIdentifier identifier) {
        return identifier.getName();
    }

    private String toString(ConditionBranch branch) {
        StringBuilder builder = new StringBuilder();

        String cond = toString(branch.getCondition());
        builder
                .append("(")
                .append(cond)
                .append(")");

        Statement body = branch.getBody();
        if (body instanceof CompoundStatement compStmt) {
            // Если телом ветки является блок кода, то необходимо определить
            // куда нужно добавить фигурные скобки и добавить само тело
            // Пример (для случая, когда скобка на той же строке):
            // if (a > b) {
            //     max = a;
            // }
            builder
                    .append(_openBracketOnSameLine ? " " : "\n")
                    .append(toString(compStmt));
        }
        else {
            // В случае если тело ветки не блок кода, то добавляем отступ
            // и вставляем тело
            // Пример:
            // if (a > b)
            //     max = a;
            increaseIndentLevel();
            builder.append("\n").append(indent(toString(body)));
            decreaseIndentLevel();
        }

        return builder.toString();
    }

    private String toString(BinaryComparison binComp) {
        return switch (binComp) {
            case EqOp op -> toString(op);
            case GeOp op -> toString(op);
            case GtOp op -> toString(op);
            case LeOp op -> toString(op);
            case LtOp op -> toString(op);
            case NotEqOp op -> toString(op);
            default -> throw new IllegalStateException("Unexpected value: " + binComp);
        };
    }

    public String toString(CompoundComparison cmp) {
        StringBuilder builder = new StringBuilder();

        for (BinaryComparison binComp : cmp.getComparisons()) {
            builder.append(toString(binComp)).append(" && ");
        }

        builder.delete(builder.length() - 4, builder.length());

        return builder.toString();
    }

    public String toString(IfStatement stmt) {
        StringBuilder builder = new StringBuilder();

        builder.append("if ");
        List<ConditionBranch> branches = stmt.getBranches();
        builder.append(toString(branches.getFirst())).append("\n");

        for (ConditionBranch branch : branches.subList(1, branches.size())) {
            builder
                    .append(indent("else if "))
                    .append(toString(branch))
                    .append("\n");
        }

        if (stmt.hasElseBranch()) {
            builder.append(indent("else"));

            Statement elseBranch = stmt.getElseBranch();
            if (elseBranch instanceof IfStatement innerIfStmt) {
                builder
                        .append(" ")
                        .append(toString(innerIfStmt));
            }
            else if (elseBranch instanceof CompoundStatement innerCompStmt) {
                builder.append(_openBracketOnSameLine ? " " : "\n");
                builder.append(toString(innerCompStmt));
            }
            else {
                builder
                        .append("\n")
                        .append(toString(elseBranch));
            }
        }
        else {
            // Удаляем лишний перевод строки, если ветки else нет
            builder.deleteCharAt(builder.length() - 1);
        }

        return builder.toString();
    }

    private String toString(HasInitialization init) {
        return switch (init) {
            case AssignmentExpression expr -> toString(expr);
            case AssignmentStatement stmt -> toString(stmt);
            case VariableDeclaration decl -> toString(decl);
            default -> throw new IllegalStateException("Unexpected value: " + init);
        };
    }

    public String toString(GeneralForLoop generalForLoop) {
        StringBuilder builder = new StringBuilder();

        builder.append("for (");

        boolean addSemi = true;
        if (generalForLoop.hasInitializer()) {
            String init = toString(generalForLoop.getInitializer());
            if (init.stripTrailing().endsWith(";")) {
                addSemi = false;
            }
            builder.append(init);
        }
        if (addSemi) {
            builder.append("; ");
        }
        else {
            builder.append(" ");
        }

        if (generalForLoop.hasCondition()) {
            String condition = toString(generalForLoop.getCondition());
            builder.append(condition);
        }
        builder.append("; ");

        if (generalForLoop.hasUpdate()) {
            String update = toString(generalForLoop.getUpdate());
            builder.append(update);
        }

        Statement body = generalForLoop.getBody();
        if (body instanceof CompoundStatement compoundStatement) {
            builder
                    .append(")")
                    .append(_openBracketOnSameLine ? " " : "\n")
                    .append(toString(compoundStatement));
        }
        else {
            builder.append(")\n");
            increaseIndentLevel();
            builder.append(indent(toString(body)));
            decreaseIndentLevel();
        }

        return builder.toString();
    }

    private String getForRangeUpdate(RangeForLoop forRangeLoop) {
        if (forRangeLoop.getRangeType() == RangeForLoop.RANGE_TYPE.UP) {
            int stepValue = forRangeLoop.getStepValue();

            if (stepValue == 1) {
                return String.format("%s++", toString(forRangeLoop.getIdentifier()));
            }
            else {
                return String.format("%s += %d", toString(forRangeLoop.getIdentifier()), stepValue);
            }
        }
        else if (forRangeLoop.getRangeType() == RangeForLoop.RANGE_TYPE.DOWN) {
            int stepValue = forRangeLoop.getStepValue();

            if (stepValue == 1) {
                return String.format("%s--", toString(forRangeLoop.getIdentifier()));
            }
            else {
                return String.format("%s -= %d", toString(forRangeLoop.getIdentifier()), stepValue);
            }
        }

        throw new RuntimeException("Can't determine range type in for loop");
    }

    private String getForRangeHeader(RangeForLoop forRangeLoop) {
        if (forRangeLoop.getRangeType() == RangeForLoop.RANGE_TYPE.UP) {
            String header = "int %s = %s; %s < %s; %s";
            return header.formatted(
                    toString(forRangeLoop.getIdentifier()),
                    toString(forRangeLoop.getStart()),
                    toString(forRangeLoop.getIdentifier()),
                    toString(forRangeLoop.getEnd()),
                    getForRangeUpdate(forRangeLoop)
            );
        }
        else if (forRangeLoop.getRangeType() == RangeForLoop.RANGE_TYPE.DOWN) {
            String header = "int %s = %s; %s > %s; %s";
            return header.formatted(
                    toString(forRangeLoop.getIdentifier()),
                    toString(forRangeLoop.getStart()),
                    toString(forRangeLoop.getIdentifier()),
                    toString(forRangeLoop.getEnd()),
                    getForRangeUpdate(forRangeLoop)
            );
        }

        throw new RuntimeException("Can't determine range type in for loop");
    }

    public String toString(RangeForLoop forRangeLoop) {
        String header = "for (" + getForRangeHeader(forRangeLoop) + ")";

        Statement body = forRangeLoop.getBody();
        if (body instanceof CompoundStatement compStmt) {
            return header + (_openBracketOnSameLine ? " " : "\n") + toString(compStmt);
        }
        else {
            increaseIndentLevel();
            String result = header + "\n" + indent(toString(body));
            decreaseIndentLevel();
            return result;
        }
    }

    public String toString(ProgramEntryPoint entryPoint) {
        StringBuilder builder = new StringBuilder();
        for (Node node : entryPoint.getBody()) {
            builder.append("%s\n".formatted(toString(node)));
        }
        return builder.toString();
    }

    public String toString(ScopedIdentifier scopedIdent) {
        StringBuilder builder = new StringBuilder();

        for (var ident : scopedIdent.getScopeResolution()) {
            builder.append(toString(ident)).append(".");
        }
        builder.deleteCharAt(builder.length() - 1); // Удаляем последнюю точку

        return builder.toString();
    }

    public String toString(FunctionCall funcCall) {
        StringBuilder builder = new StringBuilder();

        builder.append(toString(funcCall.getFunctionName())).append("(");
        for (Expression expr : funcCall.getArguments()) {
            builder.append(toString(expr)).append(", ");
        }

        if (!funcCall.getArguments().isEmpty()) {
            // Удаляем два последних символа - запятую и пробел
            builder.deleteCharAt(builder.length() - 1);
            builder.deleteCharAt(builder.length() - 1);
        }
        builder.append(")");

        return builder.toString();
    }

    public String toString(WhileLoop whileLoop) {
        String header = "while (" + toString(whileLoop.getCondition()) + ")";

        Statement body = whileLoop.getBody();
        if (body instanceof CompoundStatement compStmt) {
            return header + (_openBracketOnSameLine ? " " : "\n") + toString(compStmt);
        }
        else {
            increaseIndentLevel();
            String result = header + "\n" + indent(toString(body));
            decreaseIndentLevel();
            return result;
        }
    }

    private String toString(PostfixIncrementOp inc) {
        return toString(inc.getArgument()) + "++";
    }

    private String toString(PostfixDecrementOp dec) {
        return toString(dec.getArgument()) + "--";
    }

    private String toString(PrefixIncrementOp inc) {
        return "++" + toString(inc.getArgument());
    }

    private String toString(PrefixDecrementOp dec) {
        return "--" + toString(dec.getArgument());
    }

    private String toString(PowOp op) {
        // TODO: убедится, что импортирован модуль Math
        return "Math.pow(%s, %s)".formatted(toString(op.getLeft()), toString(op.getRight()));
    }

    private String toString(PackageDeclaration decl) {
        return "package %s;".formatted(toString(decl.getPackageName()));
    }
}
