package org.vstu.meaningtree.nodes.statements;

import org.jetbrains.annotations.NotNull;
import org.vstu.meaningtree.nodes.Node;
import org.vstu.meaningtree.nodes.Statement;
import org.vstu.meaningtree.nodes.interfaces.HasSymbolScope;
import org.vstu.meaningtree.utils.TreeNode;
import org.vstu.meaningtree.utils.env.SymbolEnvironment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CompoundStatement extends Statement implements Iterable<Node>, HasSymbolScope {
    @TreeNode private List<Node> nodes;
    private final SymbolEnvironment _env;

    public CompoundStatement(SymbolEnvironment env, Node... nodes) {
        this(env, List.of(nodes));
    }

    public CompoundStatement(SymbolEnvironment env, List<Node> nodes) {
        _env = env;
        this.nodes = new ArrayList<>(nodes);
    }

    @Override
    public String generateDot() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%s [label=\"%s\"];", _id, getClass().getSimpleName()));
        for (Node node : nodes) {
            builder.append(node.generateDot());
            builder.append(String.format("%s -- %s;\n", _id, node.getId()));
        }
        return builder.toString();
    }

    @NotNull
    @Override
    public Iterator<Node> iterator() {
        return nodes.iterator();
    }

    public int getLength() {
        return nodes.size();
    }

    public Node[] getNodes() {
        return nodes.toArray(new Node[0]);
    }

    public void substitute(int index, Node node) {
        nodes.set(index, node);
    }

    public void insert(int index, Node node) {
        nodes.add(index, node);
    }

    @Override
    public SymbolEnvironment getEnv() {
        return _env;
    }
}
