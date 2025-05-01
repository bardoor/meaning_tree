package org.vstu.meaningtree.nodes.expressions.other;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.utils.TreeNode;

import java.util.Objects;

public class IndexExpression extends Expression {
    @TreeNode private Expression expr;
    // index может содержать ExpressionSequence
    @TreeNode private Expression index;

    private boolean _preferPointers = false;

    public IndexExpression(Expression expr, Expression index) {
        this.expr = expr;
        this.index = index;
    }

    public IndexExpression(Expression expr, Expression index, boolean preferPointers) {
        this.expr = expr;
        this.index = index;
        _preferPointers = preferPointers;
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }

    public Expression getExpr() {
        return expr;
    }

    public Expression getIndex() {
        return index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IndexExpression that = (IndexExpression) o;
        return Objects.equals(expr, that.expr) && Objects.equals(index, that.index) && _preferPointers == that._preferPointers;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), expr, index, _preferPointers);
    }

    @Override
    public IndexExpression clone() {
        IndexExpression obj = (IndexExpression) super.clone();
        obj.expr = expr.clone();
        obj.index = index.clone();
        obj._preferPointers = _preferPointers;
        return obj;
    }

    public boolean isPreferPointerRepresentation() {
        return _preferPointers;
    }
}
