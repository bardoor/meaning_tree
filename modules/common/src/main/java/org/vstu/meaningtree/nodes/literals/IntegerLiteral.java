package org.vstu.meaningtree.nodes.literals;

import java.util.Objects;

public class IntegerLiteral extends NumericLiteral {
    private final int _value;

    public IntegerLiteral(String s) {
        _value = Integer.parseInt(s);
    }

    @Override
    public Number getValue() {
        return _value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntegerLiteral that = (IntegerLiteral) o;
        return _value == that._value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(_value);
    }
}
