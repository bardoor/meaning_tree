package org.vstu.meaningtree.nodes;

abstract public class UnaryExpression extends Expression {
    private final Expression _argument;

    public UnaryExpression(Expression argument) {
        _argument = argument;
    }

    public Expression getArgument() {
        return _argument;
    }
}
