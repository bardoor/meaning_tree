package org.vstu.meaningtree.nodes.types;

import org.vstu.meaningtree.nodes.Type;

public class UnmodifiableListType extends PlainCollectionType {
    public UnmodifiableListType(Type itemType) {
        super(itemType);
    }
}
