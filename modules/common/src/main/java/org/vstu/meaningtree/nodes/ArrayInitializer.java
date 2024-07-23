package org.vstu.meaningtree.nodes;

import java.util.List;

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
}
