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
}
