package org.vstu.meaningtree;

import org.vstu.meaningtree.nodes.Node;

import java.io.Serializable;
import java.util.Objects;

public class MeaningTree implements Serializable, Cloneable {
    private final Node _rootNode;

    public MeaningTree(Node rootNode) {
        _rootNode = rootNode;
    }

    public Node getRootNode() {
        return _rootNode;
    }

    /*TODO: uncomment when NodeIterator will be fixed
    @Override
    @NotNull
    public Iterator<Node.Info> iterator() {
        return _rootNode.iterateChildren();
    }

    public List<Node.Info> walk() {
        return _rootNode.walkChildren();
    }
    */



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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MeaningTree that = (MeaningTree) o;
        return Objects.equals(_rootNode, that._rootNode);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(_rootNode);
    }

    @Override
    public MeaningTree clone() {
        return new MeaningTree(_rootNode.clone());
    }
}

