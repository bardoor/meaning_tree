package org.vstu.meaningtree.nodes.literals;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class IntegerLiteral extends NumericLiteral {
    public enum Representation {
        DECIMAL,
        HEX,
        BINARY,
        OCTAL
    }

    private boolean _isLongNumber;
    private final long _value;
    private Representation _repr;


    public IntegerLiteral(String s) {
        _value = parseValue(s);
    }

    public long parseValue(String s) {
        int base = 10;
        s = s.toLowerCase();
        if (s.startsWith("0b")) {
            base = 2;
            _repr = Representation.BINARY;
        } else if (s.startsWith("0o")) {
            base = 8;
            _repr = Representation.OCTAL;
        } else if (s.startsWith("0x")) {
            base = 16;
            _repr = Representation.HEX;
        }
        s = StringUtils.removeStart(s, "0b");
        s = StringUtils.removeStart(s, "0o");
        s = StringUtils.removeStart(s, "0x");
        if (s.endsWith("l")) {
            _isLongNumber = true;
        } else {
            _isLongNumber = false;
        }
        s = StringUtils.removeEnd(s, "l");
        return Long.parseLong(s, base);
    }

    @Override
    public Number getValue() {
        return _value;
    }

    public long getLongValue() {
        return _value;
    }

    @Override
    public String getStringValue() {
        StringBuilder builder = new StringBuilder();
        int base = switch (_repr) {
            case DECIMAL -> 10;
            case HEX -> 16;
            case BINARY -> 2;
            case OCTAL -> 8;
        };
        switch (base) {
            case 2 -> builder.append("0b");
            case 8 -> builder.append("0o");
            case 16 -> builder.append("0x");
        }
        builder.append(Long.toString(getLongValue(), base));
        if (_isLongNumber) {
            builder.append("L");
        }
        return builder.toString();
    }

    public Representation getIntegerRepresentation() {
        return _repr;
    }

    public boolean isLong() {
        return _isLongNumber;
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
