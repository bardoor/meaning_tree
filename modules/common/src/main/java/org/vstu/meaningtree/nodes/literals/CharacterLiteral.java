package org.vstu.meaningtree.nodes.literals;

public class CharacterLiteral extends Literal {
    private final int _value;

    public CharacterLiteral(int codePoint) {
        _value = codePoint;
    }

    public int getValue() {
        return _value;
    }
}
