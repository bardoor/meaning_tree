package org.vstu.meaningtree.serializers.model;

import org.vstu.meaningtree.nodes.Node;

public interface Deserializer<T> {
    Node deserialize(T serialized);
}
