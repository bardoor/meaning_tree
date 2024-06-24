package org.vstu.meaningtree.nodes;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

abstract public class Node implements Serializable {
    protected static AtomicInteger _id_generator = new AtomicInteger();
    protected Integer _id = _id_generator.incrementAndGet();

    public String generateDot() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%s [label=\"%s\"];\n", _id, getClass().getSimpleName()));
        Map<String, Node> nodes = getChildren();
        for (String fieldName : nodes.keySet()) {
            Node node = nodes.get(fieldName);
            builder.append(node.generateDot());
            builder.append(String.format("%s -- %s [label=\"%s\"];\n", _id, node.getId(), fieldName));
        }
        return builder.toString();
    }

    public Integer getId() {
        return _id;
    }

    public SortedMap<String, Node> getChildren() {
        SortedMap<String, Node> map = new TreeMap<>();
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                if (field.get(this) instanceof Node node) {
                    map.put(field.getName(), node);
                }
            } catch (IllegalAccessException ignored) {}
        }
        return map;
    }
}
