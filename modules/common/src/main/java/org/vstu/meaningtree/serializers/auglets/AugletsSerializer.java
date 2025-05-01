package org.vstu.meaningtree.serializers.auglets;

import org.vstu.meaningtree.nodes.Node;
import org.vstu.meaningtree.serializers.model.Serializer;
import org.vstu.meaningtree.utils.auglets.AugletsMeta;

public class AugletsSerializer implements Serializer<String> {
    private AugletsMeta meta;

    @Override
    public String serialize(Node node) {
        if (meta == null) {
            throw new IllegalStateException("Meta was not set. Please call AugletsSerializer.setMeta() first");
        }

    }
}
