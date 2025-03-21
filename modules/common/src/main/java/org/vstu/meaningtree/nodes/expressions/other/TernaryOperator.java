package org.vstu.meaningtree.nodes.expressions.other;

import org.vstu.meaningtree.nodes.Expression;

import java.util.Objects;

public class TernaryOperator extends Expression {
    private Expression _condition;
    private Expression _thenExpr;
    private Expression _elseExpr;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TernaryOperator that = (TernaryOperator) o;
        return Objects.equals(_condition, that._condition) && Objects.equals(_thenExpr, that._thenExpr) && Objects.equals(_elseExpr, that._elseExpr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), _condition, _thenExpr, _elseExpr);
    }

    @Override
    public TernaryOperator clone() {
        TernaryOperator obj = (TernaryOperator) super.clone();
        obj._condition = _condition.clone();
        obj._elseExpr = _elseExpr.clone();
        obj._thenExpr = _thenExpr.clone();
        return obj;
    }
}
