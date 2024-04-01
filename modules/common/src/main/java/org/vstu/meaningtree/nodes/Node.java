package org.vstu.meaningtree.nodes;

import java.util.UUID;

abstract public class Node {
    protected String _id = UUID.randomUUID().toString();
    public abstract String generateDot();
    public String getId() {
        return _id;
    }
}
