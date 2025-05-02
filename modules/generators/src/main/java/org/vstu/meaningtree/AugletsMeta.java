package org.vstu.meaningtree;

import org.vstu.meaningtree.nodes.Node;

import java.util.ArrayList;

public record AugletsMeta(
        ArrayList<Node> uniqueProblemNodes,
        ArrayList<Node> uniqueSolutionNodes) {
    public AugletsMeta() {
        this(new ArrayList<>(), new ArrayList<>());
    }
}
