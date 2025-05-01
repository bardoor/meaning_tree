package org.vstu.meaningtree;

import org.jetbrains.annotations.NotNull;
import org.vstu.meaningtree.nodes.Node;
import org.vstu.meaningtree.utils.*;

import java.io.Serializable;
import java.util.*;

public class MeaningTree implements Serializable, LabelAttachable, Cloneable, Iterable<Node.Info> {
    @TreeNode private Node rootNode;
    private TreeMap<Long, Node.Info> _index = null;
    private Set<Label> _labels = new HashSet<>();

    public MeaningTree(Node rootNode) {
        this.rootNode = rootNode;
    }

    public Node getRootNode() {
        return rootNode;
    }

    public void changeRoot(Node node) {
        rootNode = node;}

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
        } else if (source.id() == rootNode.getId()) {
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
            return new NodeIterator(rootNode, true);
        } else {
            return _index.sequencedValues().iterator();
        }
    }

    public List<Node.Info> walk() {
        ArrayList<Node.Info> nodes = new ArrayList<>(rootNode.walkChildren());
        nodes.addFirst(new Node.Info(rootNode, null, -1, "root", 0));
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
        return normalizeDot("graph MeaningTree {\ndpi=255;\n" + rootNode.generateDot() + "}");
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
        return Objects.equals(rootNode, that.rootNode);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(rootNode);
    }

    @Override
    public MeaningTree clone() {
        MeaningTree mt = new MeaningTree(rootNode.clone());
        mt._labels = new HashSet<>(this._labels);
        return mt;
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

    @Override
    public Set<Label> getAllLabels() {
        return Set.copyOf(_labels);
    }
}

