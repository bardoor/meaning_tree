package org.vstu.meaningtree.nodes;

public class TernaryOperator extends Expression {
    private final Expression _condition;
    private final Expression _thenExpr;
    private final Expression _elseExpr;

    public TernaryOperator(Expression condition, Expression thenExpr, Expression elseExpr) {
        _condition = condition;
        _thenExpr = thenExpr;
        _elseExpr = elseExpr;
    }

    public Expression getCondition() {
        return _condition;
    }

    public Expression getThenExpr() {
        return _thenExpr;
    }

    public Expression getElseExpr() {
        return _elseExpr;
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
