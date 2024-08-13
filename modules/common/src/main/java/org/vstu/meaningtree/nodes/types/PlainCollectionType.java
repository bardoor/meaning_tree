package org.vstu.meaningtree.nodes.types;

import org.vstu.meaningtree.nodes.Type;

import java.util.Objects;

public class PlainCollectionType extends Type implements Generic {
    private final Type _itemType;

    public PlainCollectionType(Type itemType) {
        _itemType = itemType;
    }

    @Override
    public Type[] getTypeParameters() {
        return new Type[] {_itemType};
    }

    public Type getItemType() {
        return _itemType;
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlainCollectionType that = (PlainCollectionType) o;
        return Objects.equals(_itemType, that._itemType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), _itemType);
    }
}
