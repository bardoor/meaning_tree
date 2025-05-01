package org.vstu.meaningtree.nodes.types.builtin;

import org.vstu.meaningtree.nodes.Type;
import org.vstu.meaningtree.utils.TreeNode;

import java.util.Objects;

public class PointerType extends Type {
    @TreeNode private Type targetType;

    public PointerType(Type target) {
        targetType = target;
    }

    public Type getTargetType() {
        return targetType;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PointerType that = (PointerType) o;
        return Objects.equals(targetType, that.targetType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), targetType);
    }

    @Override
    public PointerType clone() {
        PointerType obj = (PointerType) super.clone();
        obj.targetType = targetType.clone();
        return obj;
    }
}
