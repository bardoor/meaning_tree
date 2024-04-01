package org.vstu.meaningtree.languages.viewers;

import org.vstu.meaningtree.ParenthesizedExpression;
import org.vstu.meaningtree.Type;
import org.vstu.meaningtree.VariableDeclaration;
import org.vstu.meaningtree.languages.viewers.Viewer;
import org.vstu.meaningtree.nodes.AssignmentExpression;
import org.vstu.meaningtree.nodes.BinaryExpression;
import org.vstu.meaningtree.nodes.Node;
import org.vstu.meaningtree.nodes.comparison.*;
import org.vstu.meaningtree.nodes.literals.FloatLiteral;
import org.vstu.meaningtree.nodes.literals.IntegerLiteral;
import org.vstu.meaningtree.nodes.literals.StringLiteral;
import org.vstu.meaningtree.nodes.logical.ShortCircuitAndOp;
import org.vstu.meaningtree.nodes.logical.ShortCircuitOrOp;
import org.vstu.meaningtree.nodes.math.*;
import org.vstu.meaningtree.nodes.statements.AssignmentStatement;
import org.vstu.meaningtree.nodes.statements.CompoundStatement;
import org.vstu.meaningtree.nodes.types.FloatType;
import org.vstu.meaningtree.nodes.types.IntType;

public class JavaViewer extends Viewer {

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
            case ParenthesizedExpression expr -> toString(expr);
            case AssignmentExpression expr -> toString(expr);
            case AssignmentStatement stmt -> toString(stmt);
            case VariableDeclaration stmt -> toString(stmt);
            case CompoundStatement stmt -> toString(stmt);
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

    public String toString(VariableDeclaration stmt) {
        if (stmt.hasInitializer()) {
            return String.format("%s %s = %s;", toString(stmt.getType()), toString(stmt.getName()), toString(stmt.getRValue()));
        }

        return String.format("%s %s;", toString(stmt.getType()), toString(stmt.getName()));
    }

    public String toString(CompoundStatement stmt) {
        StringBuilder builder = new StringBuilder();
        builder.append("{\n");
        for (Node node : stmt) {
            builder.append(String.format("%s\n", toString(node)));
        }
        builder.append("}\n");
        return builder.toString();
    }
}
