package org.vstu.meaningtree.iterators.utils;

import org.jetbrains.annotations.NotNull;
import org.vstu.meaningtree.nodes.Node;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;

public class NodeFieldDescriptor extends FieldDescriptor implements Iterable<FieldDescriptor> {
    public NodeFieldDescriptor(Node owner, String fieldName, Field field, boolean readOnly) {
        super(owner, fieldName, field, readOnly);
    }

    @Override
    public FieldDescriptor clone() {
        return new NodeFieldDescriptor(owner, name, field, readOnly);
    }

    @Override
    public boolean isReference() {
        return true;
    }

    public Node get() throws IllegalAccessException {
        if (field.getType() == Optional.class) {
            var opt = (Optional) field.get(owner);
            if (opt.isPresent()) {
                return (Node) opt.get();
            } else {
                return null;
            }
        }
        return (Node) field.get(owner);
    }

    @Override
    public @NotNull Iterator<FieldDescriptor> iterator() {
        try {
            return get().getFieldDescriptors().values().iterator();
        } catch (IllegalAccessException e) {
            return new ArrayList<FieldDescriptor>().iterator();
        }
    }
}
