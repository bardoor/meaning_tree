package org.vstu.meaningtree.utils;

import java.util.Objects;

/**
 * Специальные метки для узла дерева
 */
public class NodeLabel {
    /**
     * Метка, которая использует атрибут и позволяет привязать к узлу любое значение для любых целей
     */
    public static final short VALUE = 0;

    /**
     * Указывает Viewer выводить пустую строку вместо этого узла
     */
    public static final short DUMMY = 1;

    private short id;
    private Object attribute = null;

    public NodeLabel(short id, Object attribute) {
        this.attribute = attribute;
        this.id = id;
    }

    public NodeLabel(short id) {
        this.id = id;
    }

    public short getId() {
        return id;
    }

    public Object getAttribute() {
        return attribute;
    }

    public boolean hasAttribute() {
        return attribute != null;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof NodeLabel)) return false;
        return ((NodeLabel)o).id == this.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(attribute, id);
    }
}
