package org.vstu.meaningtree.nodes.types;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Type;

import java.util.Optional;

public class ArrayType extends PlainCollectionType {
    private final Optional<Expression> _size;

    public ArrayType(Type itemType, Expression size) {
        super(itemType);
        _size = Optional.ofNullable(size);
    }

    public ArrayType(Type itemType) {
        this(itemType, null);
    }

    public boolean hasSize() {
        return _size.isPresent();
    }

    public Expression getSize() {
        if (!hasSize()) {
            throw new RuntimeException("Size of array isn't present");
        }
        return _size.get();
    }
}
