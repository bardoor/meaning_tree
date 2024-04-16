package org.vstu.meaningtree.languages.viewers;

import org.vstu.meaningtree.nodes.*;
import org.vstu.meaningtree.nodes.declarations.VariableDeclaration;
import org.vstu.meaningtree.nodes.declarations.VariableDeclarator;
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
import org.vstu.meaningtree.nodes.types.FloatType;
import org.vstu.meaningtree.nodes.types.IntType;

public class JavaViewer extends Viewer {

    private static final String _INDENTATION = "    ";
    private int _indentLevel;

    public JavaViewer() {
        _indentLevel = 0;
    }

    @Override
    public String toString(Node node) {
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
            case VariableDeclaration stmt -> toString(stmt);
            case CompoundStatement stmt -> toString(stmt);
            case ExpressionStatement stmt -> toString(stmt);
            case SimpleIdentifier expr -> toString(expr);
            case IfStatement stmt -> toString(stmt);
            case GeneralForLoop stmt -> toString(stmt);
            default -> throw new IllegalStateException(String.format("Can't stringify node %s", node.getClass()));
        };
    }

    public String toString(FloatLiteral literal) {
        return String.format("%f", (double) literal.getValue());
    }

    public String toString(IntegerLiteral literal) {
        return String.format("%d", (int) literal.getValue());
    }

    public String toString(StringLiteral literal) {
        return String.format("\"%s\"", literal.getValue());
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

    public String toString(AssignmentExpression expr) {
        return String.format("%s = %s", toString(expr.getLValue()), toString(expr.getRValue()));
    }

    public String toString(AssignmentStatement stmt) {
        return String.format("%s = %s;", toString(stmt.getLValue()), toString(stmt.getRValue()));
    }

    private String toString (Type t) {
        return switch (t) {
            case FloatType floatType -> toString(floatType);
            case IntType intType -> toString(intType);
            default -> throw new IllegalStateException("Unexpected value: " + t.getClass());
        };
    }

    private String toString(FloatType t) {
        return "double";
    }

    private String toString(IntType t) {
        return "int";
    }

    private String toString(VariableDeclarator varDecl) {
        StringBuilder builder = new StringBuilder();

        String identifier = toString(varDecl.getIdentifier());
        builder.append(identifier);

        if (varDecl.hasInitialization()) {
            String init = toString(varDecl.getRValue());
            builder.append(" = ").append(init);
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
        return _INDENTATION.repeat(Math.max(0, _indentLevel)) + s;
    }

    public String toString(CompoundStatement stmt) {
        StringBuilder builder = new StringBuilder();
        builder.append(indent("{\n"));
        increaseIndentLevel();
        for (Node node : stmt) {
            String s;
            /*
                Костыль: перед блоком всегда добавляется индетация, чтобы не дублировать
                добавление в случае блока внутри блока, нужно проверять тип узла...
                Пример:
                    Это "{10 + 231; {124 + 143;} 31 + 341;}" транслируется в

                    {
                        10 + 231;
                            {
                            124 + 143;
                        }
                        31 + 341;
                    }

                    без проверки ниже...
             */
            if (node instanceof CompoundStatement
                    || node instanceof IfStatement
                    || node instanceof GeneralForLoop) {
                s = String.format("%s\n", toString(node));
            }
            else {
                s = indent(String.format("%s\n", toString(node)));
            }
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

    public String toString(IfStatement stmt) {
        //TODO: fix for new if structure
        /*
        StringBuilder builder = new StringBuilder();

        builder.append(indent("if ("));
        String condition = toString(stmt.getCondition());
        builder.append(condition);
        builder.append(")\n");

        String body = toString(stmt.getBody());
        builder.append(body);

        if (stmt.hasElseBranch()) {
            builder.append("\n");
            builder.append(indent("else\n"));
            String elseBranch = toString(stmt.getElseBranch()).stripLeading();
            builder.append(elseBranch);
        }

        return builder.toString();
        */
        throw new UnsupportedOperationException();
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

        builder.append(indent("for ("));

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
}
