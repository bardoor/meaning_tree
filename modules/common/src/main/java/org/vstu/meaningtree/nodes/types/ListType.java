package org.vstu.meaningtree.nodes.types;

import org.vstu.meaningtree.nodes.Type;

public class ListType extends PlainCollectionType {
    public ListType(Type itemType) {
        super(itemType);
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
