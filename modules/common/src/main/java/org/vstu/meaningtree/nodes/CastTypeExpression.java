package org.vstu.meaningtree.nodes;

public class CastTypeExpression extends Expression {
    private final Type _castType;
    private final Expression _value;

    public CastTypeExpression(Type castType, Expression value) {
        _castType = castType;
        _value = value;
    }

    public Type getCastType() {
        return _castType;
    }

    public Expression getValue() {
        return _value;
    }
}
