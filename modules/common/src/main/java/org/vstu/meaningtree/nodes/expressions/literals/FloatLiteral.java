package org.vstu.meaningtree.nodes.expressions.literals;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class FloatLiteral extends NumericLiteral {
    private final double _value;
    private boolean _isDoublePrecision;

    public FloatLiteral(String s, boolean isDoublePrecision) {
        _value = parseValue(s, false);
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
        _value = parseValue(s, true);
    }

    public double parseValue(String s, boolean parseModifiers) {
        if (parseModifiers) {
            _isDoublePrecision = !s.toLowerCase().endsWith("f");
            s = StringUtils.remove(s.toLowerCase(), "f");
        }
        return Double.parseDouble(s);
    }

    @Override
    public Number getValue() {
        return _value;
    }

    public double getDoubleValue() {return _value;}

    @Override
    public String getStringValue(boolean outputModifiers) {
        String s = Double.toString(_value);
        return s.concat(!isDoublePrecision() && outputModifiers ? "f" : "");
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
