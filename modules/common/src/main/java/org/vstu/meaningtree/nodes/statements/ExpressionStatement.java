package org.vstu.meaningtree.nodes.statements;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Statement;

import java.util.Objects;

public class ExpressionStatement extends Statement {
    protected final Expression _expr;

    public ExpressionStatement(Expression expr) {
        _expr = expr;
    }

    public Expression getExpression() {
        return _expr;
    }

    @Override
    public String generateDot() {
        return String.format("%s [label=\"%s\"];\n", _id, getClass().getSimpleName())
                + String.format("%s -- %s;\n", _id, _expr.getId())
                + _expr.generateDot();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ExpressionStatement that = (ExpressionStatement) o;
        return Objects.equals(_expr, that._expr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), _expr);
    }
}
