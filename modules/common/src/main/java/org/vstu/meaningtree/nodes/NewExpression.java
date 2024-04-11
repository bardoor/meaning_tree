package org.vstu.meaningtree.nodes;

public abstract class NewExpression extends Expression {
    protected NewExpression(Type type) {
        _type = type;
    }

    protected final Type _type;

    public Type getType() {
        return _type;
    }
}
