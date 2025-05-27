package org.vstu.meaningtree.nodes.expressions.other;

import org.vstu.meaningtree.iterators.utils.TreeNode;
import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Type;

import java.util.Objects;

public class CastTypeExpression extends Expression {
    @TreeNode private Type castType;
    @TreeNode private Expression value;

    public CastTypeExpression(Type castType, Expression value) {
        this.castType = castType;
        this.value = value;
    }

    public Type getCastType() {
        return castType;
    }

    public Expression getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CastTypeExpression that = (CastTypeExpression) o;
        return Objects.equals(castType, that.castType) && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), castType, value);
    }

    @Override
    public CastTypeExpression clone() {
        CastTypeExpression obj = (CastTypeExpression) super.clone();
        obj.castType = castType.clone();
        obj.value = value.clone();
        return obj;
    }
}
