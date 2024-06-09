package org.vstu.meaningtree.nodes;

import org.vstu.meaningtree.nodes.logical.ShortCircuitAndOp;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

abstract public class BinaryExpression extends Expression {
    private final Expression _left;
    private final Expression _right;

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
        if (startIndex >= array.length - 1) {
            return array[startIndex];
        }
        try {
            return whatClassNeeded.getConstructor().newInstance(array[startIndex], fromManyOperands(array, startIndex + 1, whatClassNeeded));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
