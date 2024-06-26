package org.vstu.meaningtree.languages.viewers;

import org.vstu.meaningtree.nodes.*;
import org.vstu.meaningtree.nodes.declarations.*;
import org.vstu.meaningtree.nodes.definitions.ClassDefinition;
import org.vstu.meaningtree.nodes.definitions.MethodDefinition;
import org.vstu.meaningtree.nodes.identifiers.ScopedIdentifier;
import org.vstu.meaningtree.nodes.identifiers.SimpleIdentifier;
import org.vstu.meaningtree.nodes.comparison.*;
import org.vstu.meaningtree.nodes.literals.FloatLiteral;
import org.vstu.meaningtree.nodes.literals.IntegerLiteral;
import org.vstu.meaningtree.nodes.literals.StringLiteral;
import org.vstu.meaningtree.nodes.logical.NotOp;
import org.vstu.meaningtree.nodes.logical.ShortCircuitAndOp;
import org.vstu.meaningtree.nodes.logical.ShortCircuitOrOp;
import org.vstu.meaningtree.nodes.math.*;
import org.vstu.meaningtree.nodes.statements.*;
import org.vstu.meaningtree.nodes.types.*;
import org.vstu.meaningtree.nodes.unary.PostfixDecrementOp;
import org.vstu.meaningtree.nodes.unary.PostfixIncrementOp;
import org.vstu.meaningtree.nodes.unary.PrefixDecrementOp;
import org.vstu.meaningtree.nodes.unary.PrefixIncrementOp;

import java.util.List;
import java.util.Objects;

import static org.vstu.meaningtree.nodes.AugmentedAssignmentOperator.POW;

public class JavaViewer extends Viewer {

    private final String _indentation;
    private int _indentLevel;
    private final boolean _openBracketOnSameLine;

    public JavaViewer(int indentSpaceCount, boolean openBracketOnSameLine) {
        _indentation = " ".repeat(indentSpaceCount);
        _indentLevel = 0;
        _openBracketOnSameLine = openBracketOnSameLine;
    }

    public JavaViewer() { this(4, true); }

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
            case MethodDefinition methodDefinition -> toString(methodDefinition);
            default -> throw new IllegalStateException(String.format("Can't stringify node %s", node.getClass()));
        };
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
        if (i > 1) {
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
        return "continue";
    }

    private String toString(BreakStatement stmt) {
        return "break";
    }

    private String toString(Comment comment) {
        return "\\\\ %s".formatted(comment.getEscapedContent());
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
        return String.format("%f", (double) literal.getValue());
    }

    public String toString(IntegerLiteral literal) {
        return String.format("%d", (long) literal.getValue());
    }

    public String toString(StringLiteral literal) {
        return String.format("\"%s\"", literal.getEscapedValue());
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

        builder.append(")\n");

        builder.append(toString(generalForLoop.getBody()));

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
