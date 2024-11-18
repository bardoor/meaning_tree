package org.vstu.meaningtree.nodes;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

abstract public class Node implements Serializable {
    protected static AtomicInteger _id_generator = new AtomicInteger();
    protected int _id = _id_generator.incrementAndGet();

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

    // TODO: в будущем нужно сделать возможность пройтись итератором по дереву и получить такую информацию
    /**
     * @param oneOfMany признак того, что поле, в котором он находится - массив или коллекция
     */
    public record NodeInfo(Node node, Node parent, boolean oneOfMany, String fieldName) {
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

    // TODO: Функция добавлена для необходимости получить всех детей узла без разбора.
    // По факту в будущем нужен итератор, который будет ленивым и выдавать больше информации
    // например через NodeInfo. Пока времени это реализовать нет
    public List<Node> walkAllNodes() {
        ArrayList<Node> nodes = new ArrayList<>();
        appendWalkNode(nodes, this);
        return nodes;
    }

    private void appendWalkNode(List<Node> nodes, Node node) {
        for (Object obj : node.getChildren().values()) {
            if (obj instanceof List<?> list) {
                for (Object lstChild : list) {
                    Node childNode = (Node) lstChild;
                    nodes.add(childNode);
                    appendWalkNode(nodes, childNode);
                }
            } else if (obj instanceof Map<?, ?> map) {
                for (Object lstChild : map.values()) {
                    Node childNode = (Node) lstChild;
                    nodes.add(childNode);
                    appendWalkNode(nodes, childNode);
                }
            } else if (obj instanceof Node childNode) {
                nodes.add(childNode);
                appendWalkNode(nodes, childNode);
            } else if (obj instanceof Optional<?> optional) {
                if (optional.isPresent()) {
                    Node childNode = (Node) optional.get();
                    nodes.add(childNode);
                    appendWalkNode(nodes, childNode);
                }
            }
        }
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
                        (child instanceof Map childMap && childMap.values().stream().allMatch((Object obj) -> obj instanceof Node)) ||
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

    // EXPERIMENTAL
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

    // EXPERIMENTAL
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

    public void setAssignedValueTag(Object obj) {
        assignedValueTag = obj;
    }

    public Object getAssignedValueTag() {
        return assignedValueTag;
    }

    public String getNodeUniqueName() {
        return this.getClass().getName();
    }
}
