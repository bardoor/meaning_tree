package org.vstu.meaningtree.iterators;

import org.vstu.meaningtree.iterators.utils.*;
import org.vstu.meaningtree.nodes.Node;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class NodeIterator implements Iterator<NodeInfo> {
    public final Node root;
    private final FieldDescriptor parent;

    private boolean giveRoot = false;

    protected int depth;
    protected FieldDescriptor currentField = null;
    protected int fieldIndex = -1;

    protected NodeIterator currentNested;
    protected Iterator<Node> nodeIterator;
    protected Iterator<FieldDescriptor> fieldIterator;

    public NodeIterator(Node node) {
        this(node, false, 0, null);
    }

    NodeIterator(Node node, int depth) {
        this(node, false, depth, null);
    }

    public NodeIterator(Node node, boolean includeThis) {
       this(node, includeThis, 0, null);
    }

    NodeIterator(Node node, boolean includeThis, int depth, FieldDescriptor parent) {
        this.root = node;
        this.depth = depth;
        this.fieldIterator = node.getFieldDescriptors().values().iterator();
        giveRoot = includeThis;
        this.parent = parent;
    }

    @Override
    public boolean hasNext() {
        boolean pre = giveRoot || (currentNested != null && currentNested.hasNext())
                || (nodeIterator != null && nodeIterator.hasNext());
        if (!pre && fieldIterator != null && fieldIterator.hasNext()) {
            FieldDescriptor fd = fieldIterator.next();
            currentField = fd;
            try {
                if (fd instanceof NodeFieldDescriptor nfd) {
                    currentNested = new NodeIterator(nfd.get(), true, depth + 1, fd);
                } else if (fd instanceof ArrayFieldDescriptor afd) {
                    nodeIterator = afd.iterator();
                } else if (fd instanceof CollectionFieldDescriptor cfd) {
                    nodeIterator = cfd.iterator();
                }
            } catch (IllegalAccessException e) {}
        }
        return giveRoot || (currentNested != null && currentNested.hasNext())
                || (nodeIterator != null && nodeIterator.hasNext())
                || (fieldIterator != null && fieldIterator.hasNext());
    }

    @Override
    public NodeInfo next() {
        if (giveRoot) {
            giveRoot = false;
            return new NodeInfo(root, parent == null ? null : parent.getOwner(), parent, 0);
        }
        if (currentNested != null && currentNested.hasNext()) {
            return currentNested.next();
        }
        if (nodeIterator != null && nodeIterator.hasNext()) {
            Node node = nodeIterator.next();
            fieldIndex++;
            currentNested = new NodeIterator(node, false, depth + 1, null);
            return new NodeInfo(node, root, currentField.withIndex(fieldIndex), depth);
        }
        throw new NoSuchElementException();
    }
}
