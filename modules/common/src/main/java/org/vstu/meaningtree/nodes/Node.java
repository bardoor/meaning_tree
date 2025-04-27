package org.vstu.meaningtree.nodes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.vstu.meaningtree.utils.Experimental;
import org.vstu.meaningtree.utils.Label;
import org.vstu.meaningtree.utils.LabelAttachable;
import org.vstu.meaningtree.utils.NodeIterator;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

abstract public class Node implements Serializable, Cloneable, LabelAttachable {
    protected static AtomicLong _id_generator = new AtomicLong();
    protected long _id = _id_generator.incrementAndGet();

    /**
     * Внимание! После вызова этого метода, все новые узлы дерева начнут нумерацию своего id с нуля.
     * Это может привести к конфликтам. Убедитесь, что новые узлы не будут сравниваться по id с предыдущими узлами
     */
    public static void resetIdCounter() {
        System.err.println("Warning! Node counter was reset. It may cause conflicts");
        _id_generator = new AtomicLong();
    }

    /**
     * @param index признак того, что поле, в коллекции. Индекс - для последовательных коллекций, ключ - для словарей.
     * В случае, если не в коллекции - null
     */
    public record Info(Node node, Node parent, Object index, String fieldName, int depth) {
        public String readableFieldName() {
            return fieldName.startsWith("_") ? fieldName.replaceFirst("_", "") : fieldName;
        }

        public long id() {
            return node.getId();
        }

        public boolean isInCollection() {
            return index != null;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Info info)) return false;
            return index == info.index && node.uniquenessEquals(info.node)
                    && Objects.equals(parent, info.parent) && Objects.equals(fieldName, info.fieldName);
        }

        @Override
        public int hashCode() {
            return node.hashCode();
        }
    }

    private Set<Label> _labels = new HashSet<>();

    /**
     * Проверяет значение узлов по значению
     * @param o другой объект
     * @return результат эквивалентности
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Objects.equals(_labels, node._labels);
    }

    /**
     * Уникальный хэш-код узла, исходя из его содержимого и типа
     * @return хэш-код
     */
    @Override
    public int hashCode() {
        List<Object> toHash = new ArrayList<>();
        toHash.add(getClass().getSimpleName());
        toHash.addAll(_labels);
        return Objects.hash(toHash.toArray(new Object[0]));
    }

    @Override
    public Node clone() {
        try {
            Node clone = (Node) super.clone();
            clone._id = getId();
            clone._labels = new HashSet<>(_labels);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
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

    public long getId() {
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

    /**
     * Подменяет дочерний узел данного узла указанным, если это возможно.
     * Списки и словари считаются дочерним объектом и в них функция не заходит.
     * Если нужно такое поведение см. substituteNodeChildren
     * @param fieldName - имя поля
     * @param newChild - новый дочерний узел
     * @return выполнилась ли замена
     */
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
                } catch (IllegalAccessException | IllegalArgumentException e) {
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * Подменяет дочерний узел данного узла указанным, если это возможно.
     * Данный метод может менять дочерний узел даже внутри списка или словаря, если передать key
     * @param fieldName - имя поля
     * @param newChild - новый дочерний узел
     * @param key - ключ словаря или индекс массива, если null - эквивалентно substituteChildren
     * @return выполнилась ли замена
     */
    @Experimental
    public boolean substituteNodeChildren(String fieldName, Object newChild, Object key) {
        if (key == null) {
            return substituteChildren(fieldName, newChild);
        }
        Field[] fields = getAllFields(this);
        for (Field field : fields) {
            if (field.getName().equals(fieldName)) {
                try {
                    Object value = field.get(this);
                    if (value instanceof List list) {
                        list.set((Integer)key, newChild);
                    } else if (value instanceof Map map) {
                        map.put(key, newChild);
                    }
                    return true;
                } catch (IllegalAccessException | IllegalArgumentException e) {
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
                } catch (IllegalAccessException | IllegalArgumentException ex2) {
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
        removeLabel(Label.VALUE);
        _labels.add(new Label(Label.VALUE, obj));
    }

    /**
     * Привязанный тег значения к узлу
     * @return любой объект, привязанный ранее или null
     */
    @Nullable
    public Object getAssignedValueTag() {
        Label label = getLabel(Label.VALUE);
        if (label != null) {
            return label.getAttribute();
        } else {
            return null;
        }
    }

    public String getNodeUniqueName() {
        return this.getClass().getName();
    }

    @Experimental
    @NotNull
    /**
     * Итератор может выдавать нулевые ссылки, их лучше игнорировать
     */
    public Iterator<Info> iterateChildren() {
        return new NodeIterator(this, false);
    }

    @Experimental
    public List<Node.Info> walkChildren() {
        List<Node.Info> result = new ArrayList<>();
        NodeIterator iterator = new NodeIterator(this, false);
        iterator.forEachRemaining(result::add);
        return result.stream().filter(Objects::nonNull).toList();
    }

    @Override
    public void setLabel(Label label) {
        _labels.add(label);
    }

    @Override
    public Label getLabel(short id) {
        return _labels.stream().filter((Label l) -> l.getId() == id).findFirst().orElse(null);
    }

    @Override
    public boolean hasLabel(short id) {
        return _labels.stream().anyMatch((Label l) -> l.getId() == id);
    }

    @Override
    public boolean removeLabel(Label label) {
        return _labels.remove(label);
    }

    @Deprecated
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

    @Override
    public Set<Label> getAllLabels() {
        return Set.copyOf(_labels);
    }
}
