package org.vstu.meaningtree.nodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class CompoundStatement extends Node implements Iterable<Node> {
    private final List<Node> _nodes;

    public CompoundStatement(Node... nodes) {
        _nodes = new ArrayList<>();
        _nodes.addAll(Arrays.asList(nodes));
    }

    public void add(Node node) {
        _nodes.add(node);
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<Node> iterator() {
        return _nodes.iterator();
    }
}
