package org.vstu.meaningtree.nodes;

import java.util.Objects;

public abstract class NewExpression extends Expression {
    protected NewExpression(Type type) {
        _type = type;
    }

    protected final Type _type;

    public Type getType() {
        return _type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NewExpression that = (NewExpression) o;
        return Objects.equals(_type, that._type);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(_type);
    }
}
