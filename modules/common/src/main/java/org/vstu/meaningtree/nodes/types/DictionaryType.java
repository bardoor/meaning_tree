package org.vstu.meaningtree.nodes.types;

import org.vstu.meaningtree.nodes.Type;

public class DictionaryType extends Type implements Generic {
    private final Type _keyType;
    private final Type _valueType;

    public DictionaryType(Type keyType, Type valueType) {
        _keyType = keyType;
        _valueType = valueType;
    }

    public Type getKeyType() {
        return _keyType;
    }

    public Type getValueType() {
        return _valueType;
    }

    @Override
    public Type[] getTypeParameters() {
        return new Type[] {_keyType, _valueType};
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
