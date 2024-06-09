package org.vstu.meaningtree.nodes.literals;

import org.vstu.meaningtree.nodes.Literal;

import java.util.Objects;

public class StringLiteral extends Literal {
    protected final String value;

    //TODO: Escape characters like ', "
    //TODO: What about regular expressions??

    public String getValue() {
        return value;
    }

    public StringLiteral(String value) {
        this.value = value;
    }

    public boolean isMultiline() {
        return getValue().contains("\n");
    }

    @Override
    public String generateDot() {
        return String.format("%s [label=\"%s\"];\n", _id, value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StringLiteral that = (StringLiteral) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
