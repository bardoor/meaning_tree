package org.vstu.meaningtree.nodes.types.containers;

import org.vstu.meaningtree.nodes.Type;
import org.vstu.meaningtree.nodes.interfaces.Generic;

import java.util.Objects;

public class DictionaryType extends Type implements Generic {
    private Type _keyType;
    private Type _valueType;

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DictionaryType that = (DictionaryType) o;
        return Objects.equals(_keyType, that._keyType) && Objects.equals(_valueType, that._valueType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), _keyType, _valueType);
    }

    @Override
    public DictionaryType clone() {
        DictionaryType obj = (DictionaryType) super.clone();
        obj._keyType = _keyType.clone();
        obj._valueType = _valueType.clone();
        return obj;
    }
}
