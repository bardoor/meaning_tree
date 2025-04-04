package org.vstu.meaningtree.nodes.expressions.literals;

import org.vstu.meaningtree.nodes.expressions.Literal;

import java.util.Objects;

public class BoolLiteral extends Literal {
    private boolean _state;

    public BoolLiteral(boolean state) {
        _state = state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoolLiteral that = (BoolLiteral) o;
        return _state == that._state;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), _state);
    }

    public boolean getValue() {
        return _state;
    }

    @Override
    public boolean evaluatesToBoolean() {
        return true;
    }
}
