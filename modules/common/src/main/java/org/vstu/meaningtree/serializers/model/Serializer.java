package org.vstu.meaningtree.serializers.model;

import org.vstu.meaningtree.MeaningTree;
import org.vstu.meaningtree.nodes.Node;

public interface Serializer<T> {
    T serialize(Node node);
    T serialize(MeaningTree mt);
}
