package org.vstu.meaningtree.nodes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.vstu.meaningtree.utils.Experimental;
import org.vstu.meaningtree.utils.NodeIterator;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

abstract public class Node implements Serializable, Cloneable {
    protected static AtomicInteger _id_generator = new AtomicInteger();
    protected int _id = _id_generator.incrementAndGet();

    // Любое привязанное к узлу извне значение
    @Nullable
    protected Object assignedValueTag = null;

    /**
     * Проверяет значение узлов по значению
     * @param o другой объект
     * @return результат эквивалентности
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Objects.equals(assignedValueTag, node.assignedValueTag);
    }

    /**
     * Уникальный хэш-код узла, исходя из его содержимого и типа
     * @return хэш-код
     */
    @Override
    public int hashCode() {
        return Objects.hash(assignedValueTag, getClass().getSimpleName().hashCode());
    }

    @Override
    public Node clone() {
        try {
            Node clone = (Node) super.clone();
            clone._id = getId();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    /**
     * @param pos признак того, что поле, в котором он находится - массив или коллекция. Индекс в коллекции.
     * В случае, если не в массиве, то имеет значение -1
     */
    public record Info(Node node, Node parent, int pos, String fieldName) {
    }

    public String generateDot() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%s [label=\"%s\"];\n", _id, getClass().getSimpleName()));
        Map<String, Object> nodes = getChildren();
        for (String fieldName : nodes.keySet()) {
            if (nodes.get(fieldName) instanceof Node node) {
                builder.append(node.generateDot());
                builder.append(String.format("%s -- %s [label=\"%s\"];\n", _id, node.getId(), fieldName));
            }
            // TODO: Добавить поддержку children как списков
        }
        return builder.toString();
    }

    public int getId() {
        return _id;
    }

    public boolean uniquenessEquals(Node other) {
        return this.getId() == other.getId();
    }

    /**
     * @return словарь дочерних узлов или контейнеров, состоящих из узлов данного узла. Возможные типы значений: Map, List, Node
     */
    @SuppressWarnings("unchecked")
    public SortedMap<String, Object> getChildren() {
        SortedMap<String, Object> map = new TreeMap<>();
        Field[] fields = getAllFields(this);
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object child = field.get(this);
                if (child instanceof Node ||
                        (child instanceof List collection && collection.stream().allMatch((Object obj) -> obj instanceof Node)) ||
                        (child instanceof Map childMap
                                && (childMap.values().stream().allMatch((Object obj) -> obj instanceof Node)
                                || childMap.keySet().stream().allMatch((Object obj) -> obj instanceof Node))
                        ) ||
                        (child instanceof Node[])
                ) {
                    map.put(field.getName(), child);
                } else if (child instanceof Optional<?> optional) {
                    if (optional.isPresent() && optional.get() instanceof Node) {
                        map.put(field.getName(), optional.get());
                    } else {
                        map.put(field.getName(), null);
                    }
                }
            } catch (IllegalAccessException ignored) {}
        }
        return map;
    }

    private static Field[] getAllFields(Node instance) {
        List<Field> fields = new ArrayList<Field>();
        for (Class<?> c = instance.getClass(); c != null; c = c.getSuperclass()) {
            fields.addAll(Arrays.asList(c.getDeclaredFields()));
        }
        return fields.toArray(new Field[0]);
    }

    @Experimental
    public boolean substituteChildren(String fieldName, Object newChild) {
        Field[] fields = getAllFields(this);
        for (Field field : fields) {
            if (field.getName().equals(fieldName)) {
                field.setAccessible(true);
                try {
                    if (field.getType().equals(Optional.class)) {
                        field.set(this, Optional.ofNullable(newChild));
                    } else {
                        field.set(this, newChild);
                    }
                    return true;
                } catch (IllegalAccessException e) {
                    return false;
                }
            }
        }
        return false;
    }

    @Experimental
    public List ensureMutableNodeListInChildren(String listName) {
        Field[] fields = getAllFields(this);
        for (Field field : fields) {
            if (field.getName().equals(listName)) {
                field.setAccessible(true);
                try {
                    if (field.get(this) instanceof List) {
                        List collection = (List) field.get(this);
                        ArrayList newList = new ArrayList(collection);
                        substituteChildren(listName, newList);
                        return newList;
                    }
                } catch (IllegalAccessException ex2) {
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * Установить привязанный тег значения к узлу. Может быть полезен для внешней модификации дерева
     * @param obj - любой объект
     */
    public void setAssignedValueTag(@Nullable Object obj) {
        assignedValueTag = obj;
    }

    /**
     * Привязанный тег значения к узлу
     * @return любой объект, привязанный ранее или null
     */
    @Nullable
    public Object getAssignedValueTag() {
        return assignedValueTag;
    }

    public String getNodeUniqueName() {
        return this.getClass().getName();
    }

    @Experimental
    @NotNull
    public Iterator<Info> iterateChildren() {
        return new NodeIterator(this);
    }

    @Experimental
    public List<Node.Info> walkChildren() {
        List<Node.Info> result = new ArrayList<>();
        NodeIterator iterator = new NodeIterator(this);
        iterator.forEachRemaining(result::add);
        return result;
    }
}
