package org.vstu.meaningtree.nodes;

import java.util.Objects;

public class IndexExpression extends Expression {
    private final Expression _expr;
    // index может содержать ExpressionSequence
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IndexExpression that = (IndexExpression) o;
        return Objects.equals(_expr, that._expr) && Objects.equals(_index, that._index);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_expr, _index);
    }
}
