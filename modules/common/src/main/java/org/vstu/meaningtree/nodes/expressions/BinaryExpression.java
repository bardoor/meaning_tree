package org.vstu.meaningtree.nodes.expressions;

import org.vstu.meaningtree.exceptions.MeaningTreeException;
import org.vstu.meaningtree.nodes.Expression;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

abstract public class BinaryExpression extends Expression {
    protected Expression _left;
    protected Expression _right;

    public BinaryExpression(Expression left, Expression right) {
        _left = left;
        _right = right;
    }

    public Expression getLeft() {
        return _left;
    }

    public Expression getRight() {
        return _right;
    }

    @Override
    public String generateDot() {
        return String.format("%s [label=\"%s\"];\n", _id, getClass().getSimpleName())
                + _left.generateDot()
                + _right.generateDot()
                + String.format("%s -- %s;\n", _id, _left.getId())
                + String.format("%s -- %s;\n", _id, _right.getId());
    }

    public static Expression fromManyOperands(Expression[] array, int startIndex, Class<? extends BinaryExpression> whatClassNeeded){
        if (array.length == 0) {
            throw new ArrayIndexOutOfBoundsException("Empty array has been passed");
        }

        if (startIndex >= array.length - 1) {
            return array[startIndex];
        }
        try {
            return whatClassNeeded.getConstructor(Expression.class, Expression.class).newInstance(array[startIndex], fromManyOperands(array, startIndex + 1, whatClassNeeded));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new MeaningTreeException(e);
        }
    }

    public Expression getLeftmost() {
        Expression expr = this;
        while (expr.getClass().equals(this.getClass())) {
            expr = ((BinaryExpression)expr).getLeft();
        }
        return expr;
    }

    public List<Expression> getRecursivePlainOperands() {
        ArrayList<Expression> exprs = new ArrayList<>();
        exprs.add(this.getRight());
        Expression expr = this.getLeft();
        while (expr.getClass().equals(this.getClass())) {
            expr = ((BinaryExpression)expr).getLeft();
            exprs.add(((BinaryExpression)expr).getRight());
        }
        exprs.add(expr);
        return exprs.reversed();
    }

    public Expression getRightmost() {
        Expression expr = this;
        while (expr.getClass().equals(this.getClass())) {
            expr = ((BinaryExpression)expr).getRight();
        }
        return expr;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BinaryExpression that = (BinaryExpression) o;
        return Objects.equals(_left, that._left) && Objects.equals(_right, that._right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), _left, _right);
    }

    @Override
    public BinaryExpression clone() {
        BinaryExpression obj = (BinaryExpression) super.clone();
        obj._left = _left.clone();
        obj._right = _right.clone();
        return obj;
    }
}
