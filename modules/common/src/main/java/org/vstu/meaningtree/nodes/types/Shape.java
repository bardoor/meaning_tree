package org.vstu.meaningtree.nodes.types;

import org.vstu.meaningtree.nodes.Expression;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Shape {
    // Количество измерений
    private final int _dimensionCount;
    // Размер каждого измерения
    private final List<Expression> _dimensions;

    public Shape(int dimensionCount) {
        _dimensionCount = dimensionCount;
        _dimensions = List.of();
    }

    public Shape(int dimensionCount, Expression... dimensions) {
        this(dimensionCount, List.of(dimensions));
    }

    public Shape(int dimensionCount, List<Expression> dimensions) {
        if (dimensionCount != dimensions.size()) {
            throw new IllegalArgumentException();
        }

        _dimensionCount = dimensionCount;
        _dimensions = new ArrayList<>(dimensions);
    }

    public int getDimensionCount() {
        return _dimensionCount;
    }

    public Optional<Expression> getDimension(int index) {
        try {
            return Optional.ofNullable(_dimensions.get(index));
        }
        catch (IndexOutOfBoundsException exception) {
            return Optional.empty();
        }
    }

    public List<Expression> getDimensions() {
        return _dimensions;
    }
}
