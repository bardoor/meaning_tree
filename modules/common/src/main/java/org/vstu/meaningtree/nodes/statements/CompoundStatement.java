package org.vstu.meaningtree.nodes.statements;

import org.vstu.meaningtree.nodes.Node;
import org.vstu.meaningtree.nodes.Statement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class CompoundStatement extends Statement implements Iterable<Node> {
    private final List<Node> _nodes;

    public CompoundStatement(Node... nodes) {
        this(List.of(nodes));
    }

    public CompoundStatement(List<Node> nodes) {
        _nodes = List.copyOf(nodes);
    }

    public void add(Node node) {
        _nodes.add(node);
    }

    @Override
    public String generateDot() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%s [label=\"%s\"];", _id, getClass().getSimpleName()));
        for (Node node : _nodes) {
            builder.append(node.generateDot());
            builder.append(String.format("%s -- %s;\n", _id, node.getId()));
        }
        return builder.toString();
    }

    @Override
    public Iterator<Node> iterator() {
        return _nodes.iterator();
    }

    public int getLength() {
        return _nodes.size();
    }

    public Node[] getNodes() {
        return _nodes.toArray(new Node[0]);
    }
}
