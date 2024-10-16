package org.vstu.meaningtree.nodes.expressions;

import org.vstu.meaningtree.exceptions.MeaningTreeException;
import org.vstu.meaningtree.nodes.Expression;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

abstract public class BinaryExpression extends Expression {
    protected final Expression _left;
    protected final Expression _right;

    public BinaryExpression(Expression left, Expression right) {
        _left = left;
        _right = right;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BinaryExpression that = (BinaryExpression) o;
        return Objects.equals(_left, that._left) && Objects.equals(_right, that._right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_left, _right);
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
}
