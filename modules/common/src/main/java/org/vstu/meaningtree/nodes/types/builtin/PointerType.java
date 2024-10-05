package org.vstu.meaningtree.nodes.types.builtin;

import org.vstu.meaningtree.nodes.Type;

public class PointerType extends Type {
    private final Type _targetType;

    public PointerType(Type target) {
        _targetType = target;
    }

    public Type getTargetType() {
        return _targetType;
    }
}
