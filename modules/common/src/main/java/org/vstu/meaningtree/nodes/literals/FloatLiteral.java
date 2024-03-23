package org.vstu.meaningtree.nodes.literals;

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
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
