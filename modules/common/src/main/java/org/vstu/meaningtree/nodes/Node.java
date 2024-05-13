package org.vstu.meaningtree.nodes;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;

abstract public class Node {
    protected static AtomicInteger _id_generator = new AtomicInteger();
    protected Integer _id = _id_generator.incrementAndGet();

    public String generateDot(){
        StringBuilder builder = new StringBuilder();

        builder.append(String.format("%s [label=\"%s\"];\n", _id, getClass().getSimpleName()));
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Node node = null;
            try {
                if (field.get(this) instanceof Node) {
                    node = (Node) field.get(this);
                }
            } catch (IllegalAccessException ignored) {}
            if (node != null) {
                builder.append(node.generateDot());
                builder.append(String.format("%s -- %s [label=\"%s\"];\n", _id, node.getId(), field.getName()));
            }
        }

        return builder.toString();
    }

    public Integer getId() {
        return _id;
    }
}
