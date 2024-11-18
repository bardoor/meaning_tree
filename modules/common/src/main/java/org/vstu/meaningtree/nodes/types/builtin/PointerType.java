package org.vstu.meaningtree.nodes.types.builtin;

import org.vstu.meaningtree.nodes.Type;

import java.util.Objects;

public class PointerType extends Type {
    private final Type _targetType;

    public PointerType(Type target) {
        _targetType = target;
    }

    public Type getTargetType() {
        return _targetType;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PointerType that = (PointerType) o;
        return Objects.equals(_targetType, that._targetType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), _targetType);
    }
}
