package org.vstu.meaningtree.nodes.literals;

import java.util.Objects;

public class FloatLiteral extends NumericLiteral {
    private final double _value;

    public FloatLiteral(String s) {
        _value = Double.parseDouble(s);
    }

    @Override
    public Number getValue() {
        return _value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FloatLiteral that = (FloatLiteral) o;
        return Double.compare(_value, that._value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(_value);
    }
}
