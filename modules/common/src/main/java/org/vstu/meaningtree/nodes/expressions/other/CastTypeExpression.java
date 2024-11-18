package org.vstu.meaningtree.nodes.expressions.other;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Type;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CastTypeExpression that = (CastTypeExpression) o;
        return Objects.equals(_castType, that._castType) && Objects.equals(_value, that._value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), _castType, _value);
    }
}
