package org.vstu.meaningtree.iterators.utils;

import org.vstu.meaningtree.nodes.Node;


public record NodeInfo(Node node, Node parent, FieldDescriptor field, int depth) {
    public long id() {
        return node.getId();
    }

    public boolean isInCollection() {
        return field instanceof ArrayFieldDescriptor || field instanceof CollectionFieldDescriptor;
    }
}
