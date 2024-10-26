package org.vstu.meaningtree.serializers.model;

import java.util.Map;
import java.util.Objects;

public abstract class AbstractSerializedNode {
    public final Map<String, Object> values;

    protected AbstractSerializedNode(Map<String, Object> values) {
        this.values = values;
    }

    public abstract boolean hasManyNodes();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractSerializedNode that = (AbstractSerializedNode) o;
        return Objects.equals(values, that.values);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(values);
    }
}
