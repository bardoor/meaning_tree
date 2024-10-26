package org.vstu.meaningtree.serializers.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SerializedNode extends AbstractSerializedNode {
    public final String nodeName;
    public final Map<String, AbstractSerializedNode> fields;

    public SerializedNode(String nodeName, Map<String, AbstractSerializedNode> fields) {
        super(new HashMap<>());
        this.nodeName = nodeName;
        this.fields = fields;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SerializedNode that = (SerializedNode) o;
        return Objects.equals(nodeName, that.nodeName) && Objects.equals(fields, that.fields);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), nodeName, fields);
    }

    public SerializedNode(String nodeName, Map<String, AbstractSerializedNode> fields, Map<String, Object> values) {
        super(values);
        this.nodeName = nodeName;
        this.fields = fields;
    }


    @Override
    public boolean hasManyNodes() {
        return false;
    }
}