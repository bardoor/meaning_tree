package org.vstu.meaningtree;

import org.vstu.meaningtree.nodes.Node;

public class MeaningTree {
    private final Node _rootNode;

    public MeaningTree(Node rootNode) {
        _rootNode = rootNode;
    }

    public Node getRootNode() {
        return _rootNode;
    }
}

