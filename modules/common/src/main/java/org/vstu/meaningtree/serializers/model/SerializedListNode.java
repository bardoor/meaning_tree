package org.vstu.meaningtree.serializers.model;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class SerializedListNode extends AbstractSerializedNode implements Iterable<AbstractSerializedNode> {
    public final List<AbstractSerializedNode> nodes;

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

    public SerializedListNode(AbstractSerializedNode ... nodes) {
        super(new HashMap<>());
        this.nodes = Arrays.asList(nodes);
    }

    public SerializedListNode(List<? extends AbstractSerializedNode> list) {
        super(new HashMap<>());
        nodes = new ArrayList<>(list);
    }

    @Override
    public boolean hasManyNodes() {
        return true;
    }

    @Override
    public @NotNull Iterator<AbstractSerializedNode> iterator() {
        return nodes.iterator();
    }
}