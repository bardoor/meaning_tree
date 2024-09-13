package org.vstu.meaningtree.nodes.expressions.literals;

import java.util.Objects;

public class FloatLiteral extends NumericLiteral {
    private final double _value;
    private final boolean _isDoublePrecision;

    public FloatLiteral(String s, boolean isDoublePrecision) {
        _value = Double.parseDouble(s);
        _isDoublePrecision = isDoublePrecision;
    }

    public FloatLiteral(float fValue) {
        _value = fValue;
        _isDoublePrecision = false;
    }

    public FloatLiteral(double dValue) {
        _value = dValue;
        _isDoublePrecision = true;
    }

    public FloatLiteral(String s) {
        _value = Double.parseDouble(s);
        _isDoublePrecision = !s.toLowerCase().endsWith("f");
    }

    @Override
    public Number getValue() {
        return _value;
    }

    public double getDoubleValue() {return _value;}

    @Override
    public String getStringValue() {
        return Double.toString(_value);
    }

    public boolean isDoublePrecision() {
        return _isDoublePrecision;
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
