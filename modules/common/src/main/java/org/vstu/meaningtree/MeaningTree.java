package org.vstu.meaningtree;

import org.jetbrains.annotations.NotNull;
import org.vstu.meaningtree.nodes.Node;
import org.vstu.meaningtree.utils.Experimental;
import org.vstu.meaningtree.utils.Label;
import org.vstu.meaningtree.utils.LabelAttachable;
import org.vstu.meaningtree.utils.NodeIterator;

import java.io.Serializable;
import java.util.*;

public class MeaningTree implements Serializable, LabelAttachable, Cloneable, Iterable<Node.Info> {
    private Node _rootNode;
    private TreeMap<Long, Node.Info> _index = null;
    private Set<Label> _labels = new HashSet<>();

    public MeaningTree(Node rootNode) {
        _rootNode = rootNode;
    }

    public Node getRootNode() {
        return _rootNode;
    }

    public void changeRoot(Node node) {_rootNode = node;}

    public void makeIndex() {
        TreeMap<Long, Node.Info> treeMap = new TreeMap<>();
        for (Node.Info node : this) {
            if (node != null) {
                treeMap.put(node.node().getId(), node);
            }
        }
        _index = treeMap;
    }

    @Experimental
    public boolean substitute(long sourceId, Node target) {
        Node.Info source = getNodeById(sourceId);

        if (source == null) {
            return false;
        }

        if (source.parent() != null && !source.isInCollection()) {
            source.parent().substituteChildren(source.fieldName(), target);
        } else if (source.parent() != null) {
            source.parent().substituteNodeChildren(source.fieldName(), target, source.index());
        } else if (source.id() == _rootNode.getId()) {
            changeRoot(target);
        } else {
            return false;
        }
        return true;
    }

    public Node.Info getNodeById(long id) {
        if (_index == null) {
            makeIndex();
        }
        return _index.getOrDefault(id, null);
    }

    @Override
    @NotNull
    /**
     * Итератор может выдавать нулевые ссылки
     */
    public Iterator<Node.Info> iterator() {
        if (_index == null) {
            return new NodeIterator(_rootNode, true);
        } else {
            return _index.sequencedValues().iterator();
        }
    }

    public List<Node.Info> walk() {
        ArrayList<Node.Info> nodes = new ArrayList<>(_rootNode.walkChildren());
        nodes.addFirst(new Node.Info(_rootNode, null, -1, "root"));
        return nodes;
    }

    public Node findParentOfNode(Node node) {
        for (Node.Info inf : this) {
            if (inf.node().equals(node)) {
                return inf.parent();
            }
        }
        return null;
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

    @Override
    public void setLabel(Label label) {
        _labels.add(label);
    }

    @Override
    public Label getLabel(short id) {
        return _labels.stream().filter((Label l) -> l.getId() == id).findFirst().orElse(null);
    }

    @Override
    public boolean hasLabel(short id) {
        return _labels.stream().anyMatch((Label l) -> l.getId() == id);
    }

    @Override
    public boolean removeLabel(Label label) {
        return _labels.remove(label);
    }
}

