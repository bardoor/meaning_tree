package org.vstu.meaningtree.nodes;

import org.vstu.meaningtree.nodes.Expression;

import java.util.Objects;

public class ParenthesizedExpression extends Expression {
    private final Expression _expr;

    public ParenthesizedExpression(Expression expr) {
        _expr = expr;
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }

    public Expression getExpression() {
        return _expr;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParenthesizedExpression that = (ParenthesizedExpression) o;
        return Objects.equals(_expr, that._expr);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(_expr);
    }
}
