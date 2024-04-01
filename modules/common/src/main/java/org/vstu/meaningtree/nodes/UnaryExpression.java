package org.vstu.meaningtree.nodes;

abstract public class UnaryExpression extends Expression {
    private final Expression _argument;

    public UnaryExpression(Expression argument) {
        _argument = argument;
    }

    public Expression getArgument() {
        return _argument;
    }

    @Override
    public String generateDot() {
        return String.format("%s [label=\"%s\"]\n", _id, getClass().getSimpleName())
                + String.format("%s -> %s\n", _id, _argument.getId())
                + _argument.generateDot();
    }
}
