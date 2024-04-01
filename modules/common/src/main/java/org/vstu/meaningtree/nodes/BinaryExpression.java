package org.vstu.meaningtree.nodes;

abstract public class BinaryExpression extends Expression {
    private final Expression _left;
    private final Expression _right;

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
        return String.format("%s [label=\"%s\"]\n", _id, getClass().getSimpleName())
                + String.format("%s -> %s\n", _id, _left.getId())
                + String.format("%s -> %s\n", _id, _right.getId())
                + _left.generateDot()
                + _right.generateDot();
    }
}
