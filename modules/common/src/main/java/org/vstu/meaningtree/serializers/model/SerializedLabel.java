package org.vstu.meaningtree.serializers.model;


import org.vstu.meaningtree.utils.Label;

import java.util.HashMap;
import java.util.Map;

public class SerializedLabel extends AbstractSerializedNode {
    public SerializedLabel(Label label) {
        super(new HashMap<>());
        values.put("id", label.getId());
        if (label.hasAttribute()) {
            values.put("attr", label.getAttribute());
        }
    }

    public SerializedLabel(Map<String, Object> valueMap) {
        super(new HashMap<>());
        values.put("id", valueMap.getOrDefault("id", Short.MAX_VALUE));
        if (valueMap.containsKey("attr")) {
            values.put("attr", valueMap.get("attr"));
        }
    }

    @Override
    public boolean hasManyNodes() {
        return false;
    }

    public Label toObject() {
        Integer val = (Integer) values.getOrDefault("id", Short.MAX_VALUE);
        return new Label(val.shortValue(),
                values.getOrDefault("attr", null));
    }
}
