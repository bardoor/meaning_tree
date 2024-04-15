package org.vstu.meaningtree.nodes.types;

import org.vstu.meaningtree.nodes.Type;

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
}
