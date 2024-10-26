package org.vstu.meaningtree.serializers.model;

import java.util.*;

public class SerializedListNode extends AbstractSerializedNode {
    public final List<SerializedNode> nodes;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SerializedListNode that = (SerializedListNode) o;
        return Objects.equals(nodes, that.nodes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), nodes);
    }

    public SerializedListNode(SerializedNode ... nodes) {
        super(new HashMap<>());
        this.nodes = Arrays.asList(nodes);
    }

    public SerializedListNode(List<SerializedNode> list) {
        super(new HashMap<>());
        nodes = new ArrayList<>(list);
    }

    @Override
    public boolean hasManyNodes() {
        return true;
    }
}