package org.vstu.meaningtree.utils;

import org.vstu.meaningtree.exceptions.MeaningTreeException;
import org.vstu.meaningtree.nodes.Node;

import java.util.*;

/**
 * TODO: Итератор не совершенный и может выдавать null в некоторых ситуациях, поэтому это нужно иметь в виду
 */
public class NodeIterator implements Iterator<Node.Info> {
    private int ptr = -1;
    private Node parent;
    private SortedMap<String, Object> children;
    private ArrayDeque<Iterator<?>> iteratorQueue = new ArrayDeque<>();

    private boolean giveSelf;

    public NodeIterator(Node node, boolean giveSelf) {
        parent = node;
        children = node.getChildren();
        this.giveSelf = !giveSelf;
    }

    @Override
    public boolean hasNext() {
        return !iteratorQueue.isEmpty() || ptr + 1 < children.sequencedKeySet().size() || !giveSelf;
    }

    private String getChildren(int pos) {
        return children.sequencedKeySet().stream().toList().get(pos);
    }

    private void pushIterator(Object target) {
        if (target instanceof Node node) {
            iteratorQueue.addLast(node.iterateChildren());
        } else if (target instanceof Node[] array) {
            iteratorQueue.addLast(Arrays.stream(array).iterator());
        } else if (target instanceof List collection) {
            iteratorQueue.addLast(collection.iterator());
        } else if (target instanceof Map map && !map.isEmpty()) {
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

    private void ensureIteratorNotEmpty() {
        if (!iteratorQueue.isEmpty() && !iteratorQueue.getLast().hasNext()) {
            iteratorQueue.removeLast();
        }
    }

    private Node.Info getAt(int index) {
        Node.Info result = null;
        Iterator<?> iterator = iteratorQueue.getLast();
        Object target;
        String fieldName = getChildren(index);
        target = children.get(fieldName);
        if (iterator.hasNext() && !(target instanceof Map)) {
            Object iteratorNext = iterator.next();
            Node current;
            if (iteratorNext instanceof Node nodeObj) {
                current = nodeObj;
            } else if (iteratorNext instanceof Node.Info info) {
                current = info.node();
                result = info;
            } else {
                throw new MeaningTreeException("Unknown type from iterator");
            }
            ensureIteratorNotEmpty();
            if (result == null) {
                pushIterator(current);
                ensureIteratorNotEmpty();
            }
            return result != null ? result : new Node.Info(current, parent, getFieldIndex(target, current), fieldName);
        } else if (target instanceof Map && iterator.hasNext()) {
            Map.Entry<?, ?> current = (Map.Entry<?, ?>) iterator.next();
            ensureIteratorNotEmpty();
            if (current.getValue() instanceof Node) {
                pushIterator(current.getValue());
                ensureIteratorNotEmpty();
                result = new Node.Info((Node) current.getValue(), parent, -1, fieldName);
            } else {
                pushIterator(current.getKey());
                ensureIteratorNotEmpty();
                result = new Node.Info((Node) current.getKey(), parent, -1, fieldName);
            }
        } else {
            throw new MeaningTreeException("Iterator is broken. Report this case to developers");
        }
        return result;
    }

    @Override
    public Node.Info next() {
        if (!giveSelf) {
            giveSelf = true;
            return new Node.Info(parent, null, -1, "root");
        }
        if (!hasNext()) {
            throw new NoSuchElementException("Iteration ended");
        }
        while (iteratorQueue.isEmpty()) {
            ptr++;
            if (ptr >= children.size()) {
                return null;
            }
            Object target = children.get(getChildren(ptr));
            pushIterator(target);
            ensureIteratorNotEmpty();
            if (target instanceof Node node) {
                return new Node.Info(node, parent, -1, getChildren(ptr));
            }
        }
        Node.Info result = getAt(ptr);
        ensureIteratorNotEmpty();
        return result;
    }
}
