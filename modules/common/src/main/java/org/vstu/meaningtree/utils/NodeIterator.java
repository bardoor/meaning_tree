package org.vstu.meaningtree.utils;

import org.vstu.meaningtree.nodes.Node;

import java.util.*;

public class NodeIterator implements Iterator<Node.Info> {
    private int ptr = -1;
    private Node parent;
    private SortedMap<String, Object> children;
    private ArrayDeque<Iterator<?>> iteratorQueue = new ArrayDeque<>();

    public NodeIterator(Node node) {
        parent = node;
        children = node.getChildren();
    }

    @Override
    public boolean hasNext() {
        return ptr + 1 < children.sequencedKeySet().size() || !iteratorQueue.isEmpty();
    }

    private String getChildren(int pos) {
        return children.sequencedKeySet().stream().toList().get(pos);
    }

    private void pustIterator(Object target) {
        if (target instanceof Node node) {
            iteratorQueue.addLast(node.iterateChildren());
        } else if (target instanceof Node[] array) {
            iteratorQueue.addLast(Arrays.stream(array).iterator());
        } else if (target instanceof List collection) {
            iteratorQueue.addLast(collection.iterator());
        } else if (target instanceof Map map) {
            iteratorQueue.addLast(map.entrySet().iterator());
        } else if (target instanceof Optional<?> opt) {
            if (opt.isPresent() && opt.get() instanceof Node node) {
                iteratorQueue.addLast(node.iterateChildren());
            }
        }
    }

    private int getFieldIndex(Object target, Node node) {
        if (target instanceof List collection) {
            return collection.indexOf(node);
        } else if (target instanceof Node[] array) {
            for (int i = 0; i < array.length; i++) {
                if (array[i].uniquenessEquals(node)) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public Node.Info next() {
        if (!hasNext()) {
            throw new NoSuchElementException("Iteration ended");
        }
        Object target;
        if (iteratorQueue.isEmpty()) {
            ptr++;
            target = children.get(getChildren(ptr));
            pustIterator(target);
        }
        Iterator<?> iterator = iteratorQueue.getLast();
        String fieldName = getChildren(ptr);
        target = children.get(fieldName);
        if (iterator.hasNext() && !(target instanceof Map)) {
            Node current = (Node) iterator.next();
            if (!iterator.hasNext()) {
                iteratorQueue.removeLast();
            }
            pustIterator(current);
            return new Node.Info(current, parent, getFieldIndex(target, current), fieldName);
        } else if (target instanceof Map && iterator.hasNext()) {
            Map.Entry<?, ?> current = (Map.Entry<?, ?>) iterator.next();
            if (!iterator.hasNext()) {
                iteratorQueue.removeLast();
            }
            if (current.getValue() instanceof Node) {
                pustIterator(current.getValue());
                return new Node.Info((Node) current.getValue(), parent, -1, fieldName);
            } else {
                pustIterator(current.getKey());
                return new Node.Info((Node) current.getKey(), parent, -1, fieldName);
            }
        }
        return null; // unrecognized entry
    }
}
