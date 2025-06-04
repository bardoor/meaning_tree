package org.vstu.meaningtree.iterators;

import org.jetbrains.annotations.NotNull;
import org.vstu.meaningtree.iterators.utils.NodeInfo;
import org.vstu.meaningtree.nodes.Node;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public abstract class AbstractNodeIterator implements Iterator<NodeInfo>, Iterable<NodeInfo> {
    protected List<BiPredicate<Node, Node>> enterConditions = new ArrayList<>();

    @Override
    public @NotNull Iterator<NodeInfo> iterator() {
        return this;
    }

    public void addEnterCondition(BiPredicate<Node, Node> condition) {
        enterConditions.add(condition);
    }

    public void addEnterCondition(Predicate<Node> condition) {
        enterConditions.add((node, parent) -> condition.test(node));
    }

    public void clearEnterConditions() {
        enterConditions.clear();
    }

    public boolean checkEnterCondition(Node current, Node parent) {
        boolean all = true;
        for (BiPredicate<Node, Node> condition : enterConditions) {
            all &= condition.test(current, parent);
        }
        return all;
    }
}
