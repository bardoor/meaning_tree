package org.vstu.meaningtree.nodes;

public class IndexExpression extends Expression {
    private final Expression _expr;
    private final Expression _index;

    public IndexExpression(Expression expr, Expression index) {
        _expr = expr;
        _index = index;
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }

    public Expression getExpr() {
        return _expr;
    }

    public Expression getIndex() {
        return _index;
    }
}
