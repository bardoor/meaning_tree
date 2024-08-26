package org.vstu.meaningtree.nodes;

import org.jetbrains.annotations.Nullable;
import org.vstu.meaningtree.nodes.types.Shape;

import java.util.*;

public class ArrayNewExpression extends NewExpression {
    private final Shape _shape;
    private final ArrayInitializer _initializer;

    public ArrayNewExpression(Type type, Shape shape, ArrayInitializer initializer) {
        super(type);
        _shape = shape;
        _initializer = initializer;
    }

    public ArrayNewExpression(Type type, Shape shape) {
        this(type, shape, null);
    }

    public Shape getShape() {
       return _shape;
    }

    public int getDimensionsCount() {
        return _shape.getDimensionCount();
    }

    @Nullable
    public ArrayInitializer getInitializer() {
        return _initializer;
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ArrayNewExpression that = (ArrayNewExpression) o;
        return Objects.equals(_shape, that._shape) && Objects.equals(_initializer, that._initializer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), _shape, _initializer);
    }
}
