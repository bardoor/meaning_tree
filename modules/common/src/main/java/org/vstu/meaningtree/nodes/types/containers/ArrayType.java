package org.vstu.meaningtree.nodes.types.containers;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Type;
import org.vstu.meaningtree.nodes.types.containers.components.Shape;

import java.util.List;
import java.util.Objects;

public class ArrayType extends PlainCollectionType {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArrayType arrayType = (ArrayType) o;
        return Objects.equals(_shape, arrayType._shape);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), _shape);
    }

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