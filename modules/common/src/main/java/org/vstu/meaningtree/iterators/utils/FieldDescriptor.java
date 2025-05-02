package org.vstu.meaningtree.iterators.utils;

import org.vstu.meaningtree.nodes.Node;

import java.lang.reflect.Field;
import java.util.Objects;

public abstract class FieldDescriptor implements Cloneable {
    protected Node owner;
    protected String name;
    protected boolean readOnly;

    protected final Field field;

    private int indexTag = -1;

    public FieldDescriptor(Node owner, String fieldName, Field field, boolean readOnly) {
        this.owner = owner;
        this.name = fieldName;
        this.field = field;
        this.readOnly = readOnly;
    }

    public String getName() {
        return name;
    }

    public Node getOwner() {
        return owner;
    }

    public boolean isReference() {
        return indexTag != -1;
    }

    public int getIndex() {
        return indexTag;
    }

    public FieldDescriptor withIndex(int indexTag) {
        var clone = this.clone();
        if (clone.indexTag == -1) {
            clone.indexTag = indexTag;
        }
        return clone;
    }

    @Override
    public abstract FieldDescriptor clone();

    public boolean substitute(Node value) {
        try {
            field.set(owner, value);
            return true;
        } catch (IllegalAccessException e) {
            return false;
        }
    }

    public boolean canWrite() {
        return field.isAccessible() && !readOnly;
    }

    public boolean ensureWritable() {
        field.setAccessible(true);
        return true;
    }

    public Class<?> getType() {
        return field.getType();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof FieldDescriptor that)) return false;
        return readOnly == that.readOnly && indexTag == that.indexTag &&
                Objects.equals(owner, that.owner) &&
                Objects.equals(name, that.name) &&
                Objects.equals(field, that.field);
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner, name, field, readOnly, indexTag);
    }
}
