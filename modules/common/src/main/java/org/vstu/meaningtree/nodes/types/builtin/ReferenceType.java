package org.vstu.meaningtree.nodes.types.builtin;

import org.vstu.meaningtree.iterators.utils.TreeNode;
import org.vstu.meaningtree.nodes.Type;

import java.util.Objects;

public class ReferenceType extends Type {
    @TreeNode private Type targetType;

    public ReferenceType(Type target) {
        targetType = target;
    }

    public Type getTargetType() {
        return targetType;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ReferenceType that = (ReferenceType) o;
        return Objects.equals(targetType, that.targetType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), targetType);
    }

    @Override
    public ReferenceType clone() {
        ReferenceType obj = (ReferenceType) super.clone();
        obj.targetType = targetType.clone();
        return obj;
    }
}
