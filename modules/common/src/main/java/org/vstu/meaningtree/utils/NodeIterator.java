package org.vstu.meaningtree.utils;

import org.vstu.meaningtree.exceptions.MeaningTreeException;
import org.vstu.meaningtree.nodes.Node;

import java.util.*;

//TODO: этот класс не работает, его нужно исправлять
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
        // TODO: проблемы с данным участком
        return !iteratorQueue.isEmpty() || ptr + 1 < children.sequencedKeySet().size();
    }

    private String getChildren(int pos) {
        return children.sequencedKeySet().stream().toList().get(pos);
    }

    private void pustIterator(Object target) {
        if (target instanceof Node node && !node.getChildren().isEmpty()) {
            iteratorQueue.addLast(node.iterateChildren());
        } else if (target instanceof Node[] array && array.length > 0) {
            iteratorQueue.addLast(Arrays.stream(array).iterator());
        } else if (target instanceof List collection && !collection.isEmpty()) {
            iteratorQueue.addLast(collection.iterator());
        } else if (target instanceof Map map && !map.isEmpty()) {
            iteratorQueue.addLast(map.entrySet().iterator());
        } else if (target instanceof Optional<?> opt) {
            if (opt.isPresent() && opt.get() instanceof Node node && !node.getChildren().isEmpty()) {
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
            } else {
                throw new MeaningTreeException("Unknown type from iterator");
            }
            pustIterator(current);
            result = new Node.Info(current, parent, getFieldIndex(target, current), fieldName);
        } else if (target instanceof Map && iterator.hasNext()) {
            Map.Entry<?, ?> current = (Map.Entry<?, ?>) iterator.next();
            if (current.getValue() instanceof Node) {
                pustIterator(current.getValue());
                result = new Node.Info((Node) current.getValue(), parent, -1, fieldName);
            } else {
                pustIterator(current.getKey());
                result = new Node.Info((Node) current.getKey(), parent, -1, fieldName);
            }
        }
        return result;
    }

    @Override
    public Node.Info next() {
        if (!hasNext()) {
            throw new NoSuchElementException("Iteration ended");
        }
        if (iteratorQueue.isEmpty()) {
            ptr++;
            Object target = children.get(getChildren(ptr));
            pustIterator(target);
        }
        Node.Info result = getAt(ptr);
        Iterator<?> iterator = iteratorQueue.getLast();
        if (!iterator.hasNext()) {
            iteratorQueue.removeLast();
        }
        if (iteratorQueue.isEmpty()) {
            ptr++;
            if (ptr < children.size()) {
                Object target = children.get(getChildren(ptr));
                pustIterator(target);
            }
        }
        return result; // unrecognized entry
    }
}
