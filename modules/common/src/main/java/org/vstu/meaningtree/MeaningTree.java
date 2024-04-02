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

    public String generateDot() {
        return normalizeDot("graph MeaningTree {\ndpi=255;\n" + _rootNode.generateDot() + "}");
    }

    private static String normalizeDot(String dot) {
        String[] lines = dot.split("\n");

        StringBuilder connections = new StringBuilder();
        StringBuilder result = new StringBuilder();

        for (String line : lines) {
            if (line.contains("--") || line.equals("}")) {
                connections.append(line).append("\n");
            }
            else {
                result.append(line).append("\n");
            }
        }

        result.append(connections);

        return result.toString();
    }

}

