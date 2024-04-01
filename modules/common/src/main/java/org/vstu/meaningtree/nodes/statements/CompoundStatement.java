package org.vstu.meaningtree.nodes.statements;

import org.vstu.meaningtree.nodes.Node;

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
        StringBuilder builder = new StringBuilder();
        for (Node node : _nodes) {
            builder.append(String.format("%s -> %s\n", _id, node.getId()));
            builder.append(node.generateDot());
        }
        return builder.toString();
    }

    @Override
    public Iterator<Node> iterator() {
        return _nodes.iterator();
    }
}
