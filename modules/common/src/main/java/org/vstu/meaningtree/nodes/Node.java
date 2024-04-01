package org.vstu.meaningtree.nodes;

import java.util.concurrent.atomic.AtomicInteger;

abstract public class Node {
    protected static AtomicInteger _id_generator = new AtomicInteger();
    protected Integer _id = _id_generator.incrementAndGet();
    public abstract String generateDot();

    public Integer getId() {
        return _id;
    }
}
