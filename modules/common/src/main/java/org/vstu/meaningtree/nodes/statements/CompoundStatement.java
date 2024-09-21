package org.vstu.meaningtree.nodes.statements;

import org.vstu.meaningtree.nodes.Node;
import org.vstu.meaningtree.nodes.Statement;
import org.vstu.meaningtree.nodes.interfaces.HasSymbolScope;
import org.vstu.meaningtree.utils.env.SymbolEnvironment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CompoundStatement extends Statement implements Iterable<Node>, HasSymbolScope {
    private final List<Node> _nodes;
    private final SymbolEnvironment _env;

    public CompoundStatement(SymbolEnvironment env, Node... nodes) {
        this(env, List.of(nodes));
    }

    public CompoundStatement(SymbolEnvironment env, List<Node> nodes) {
        _env = env;
        _nodes = new ArrayList<>(nodes);
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

    public void substitute(int index, Node node) {
        _nodes.set(index, node);
    }

    public void insert(int index, Node node) {
        _nodes.add(index, node);
    }

    @Override
    public SymbolEnvironment getEnv() {
        return _env;
    }
}
