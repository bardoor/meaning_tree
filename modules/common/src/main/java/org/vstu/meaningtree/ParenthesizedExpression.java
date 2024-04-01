package org.vstu.meaningtree;

import org.vstu.meaningtree.nodes.Expression;

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
}
