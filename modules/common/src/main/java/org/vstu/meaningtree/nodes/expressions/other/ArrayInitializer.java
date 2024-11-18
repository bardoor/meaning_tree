package org.vstu.meaningtree.nodes.expressions.other;

import org.vstu.meaningtree.nodes.Expression;

import java.util.List;
import java.util.Objects;

public class ArrayInitializer extends Expression {
    private final List<Expression> _values;

    public ArrayInitializer(List<Expression> values) {
        _values = List.copyOf(values);
    }

    public List<Expression> getValues() {
        return List.copyOf(_values);
    }

    public int getValuesCount() {
        return _values.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArrayInitializer that = (ArrayInitializer) o;
        return Objects.equals(_values, that._values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), _values);
    }
}
