package org.vstu.meaningtree.nodes.expressions.newexpr;

import org.vstu.meaningtree.iterators.utils.TreeNode;
import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Type;

import java.util.Objects;

public abstract class NewExpression extends Expression {
    @TreeNode protected Type type;

    protected NewExpression(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NewExpression that = (NewExpression) o;
        return Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), type);
    }

    @Override
    public NewExpression clone() {
        NewExpression obj = (NewExpression) super.clone();
        obj.type = type.clone();
        return obj;
    }
}
