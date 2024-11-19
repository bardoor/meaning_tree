package org.vstu.meaningtree.nodes.expressions.literals;

import org.vstu.meaningtree.nodes.expressions.Literal;

import java.util.Objects;

public class CharacterLiteral extends Literal {
    private final int _value;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CharacterLiteral that = (CharacterLiteral) o;
        return _value == that._value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), _value);
    }

    public CharacterLiteral(int codePoint) {
        _value = codePoint;
    }

    public int getValue() {
        return _value;
    }
}
