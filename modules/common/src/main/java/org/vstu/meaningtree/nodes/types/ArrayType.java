package org.vstu.meaningtree.nodes.types;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Type;

import java.util.List;
import java.util.Optional;

public class ArrayType extends PlainCollectionType {
    private final Shape _shape;

    public ArrayType(Type itemType, int dimensionCount) {
        super(itemType);
        _shape = new Shape(dimensionCount);
    }

    public ArrayType(Type itemType, Expression size) {
        super(itemType);
        _shape = new Shape(1, size);
    }

    public ArrayType(Type itemType, int dimensionCount, Expression... dimensions) {
        this(itemType, dimensionCount, List.of(dimensions));
    }

    public ArrayType(Type itemType, int dimensionCount, List<Expression> dimensions) {
        super(itemType);
        _shape = new Shape(dimensionCount, dimensions);
    }

    public Shape getShape() {
        return _shape;
    }

    public int getDimensionsCount() {
        return _shape.getDimensionCount();
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
