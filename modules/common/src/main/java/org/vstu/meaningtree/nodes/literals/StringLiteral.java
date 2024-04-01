package org.vstu.meaningtree.nodes.literals;

import org.vstu.meaningtree.nodes.Literal;

public class StringLiteral extends Literal {
    protected final String value;

    public String getValue() {
        return value;
    }

    public StringLiteral(String value) {
        this.value = value;
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
