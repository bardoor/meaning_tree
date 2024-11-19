package org.vstu.meaningtree.nodes.types.builtin;

import org.vstu.meaningtree.nodes.Type;

import java.util.Objects;

public class ReferenceType extends Type {
    private Type _targetType;

    public ReferenceType(Type target) {
        _targetType = target;
    }

    public Type getTargetType() {
        return _targetType;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ReferenceType that = (ReferenceType) o;
        return Objects.equals(_targetType, that._targetType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), _targetType);
    }

    @Override
    public ReferenceType clone() {
        ReferenceType obj = (ReferenceType) super.clone();
        obj._targetType = _targetType.clone();
        return obj;
    }
}
