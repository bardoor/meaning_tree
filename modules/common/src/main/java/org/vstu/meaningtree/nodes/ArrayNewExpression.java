package org.vstu.meaningtree.nodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ArrayNewExpression extends NewExpression {
    private final Optional<Expression> _dimension;
    private final List<Expression> _initialValues;

    public ArrayNewExpression(Type type, List<Expression> initialValues) {
        super(type);
        this._dimension = Optional.empty();
        this._initialValues = new ArrayList<>(initialValues);
    }

    public ArrayNewExpression(Type type, Expression dimension) {
        super(type);
        this._dimension = Optional.of(dimension);
        _initialValues = new ArrayList<>();
    }

    public boolean hasDimension() {
        return _dimension.isPresent();
    }

    public Expression getDimension() {
        if (!hasDimension()) {
            throw new RuntimeException("No dimension of array");
        }
        return _dimension.get();
    }

    public List<Expression> getInitialArray() {
        return new ArrayList<>(_initialValues);
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
        return Objects.equals(_dimension, that._dimension) && Objects.equals(_initialValues, that._initialValues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), _dimension, _initialValues);
    }
}
