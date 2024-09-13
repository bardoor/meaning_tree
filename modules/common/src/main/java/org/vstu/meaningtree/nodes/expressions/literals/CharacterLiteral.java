package org.vstu.meaningtree.nodes.expressions.literals;

import org.vstu.meaningtree.nodes.expressions.Literal;

public class CharacterLiteral extends Literal {
    private final int _value;

    public CharacterLiteral(int codePoint) {
        _value = codePoint;
    }

    public int getValue() {
        return _value;
    }
}
