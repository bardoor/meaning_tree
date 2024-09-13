package org.vstu.meaningtree.nodes.expressions;

import org.vstu.meaningtree.nodes.Expression;

import java.util.Objects;

abstract public class UnaryExpression extends Expression {
    private final Expression _argument;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnaryExpression that = (UnaryExpression) o;
        return Objects.equals(_argument, that._argument);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(_argument);
    }

    public UnaryExpression(Expression argument) {
        _argument = argument;
    }

    public Expression getArgument() {
        return _argument;
    }

    @Override
    public String generateDot() {
        return String.format("%s [label=\"%s\"];\n", _id, getClass().getSimpleName())
                + _argument.generateDot()
                + String.format("%s -- %s;\n", _id, _argument.getId());
    }
}
