package meaning_tree.literals;

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
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
