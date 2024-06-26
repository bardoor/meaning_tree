package org.vstu.meaningtree.nodes.literals;

import org.vstu.meaningtree.nodes.Literal;

abstract public class NumericLiteral extends Literal {
    abstract public Number getValue();
    abstract public String getStringValue();

    @Override
    public String generateDot() {
        return String.format("%s [label=\"%s\"];\n", _id, getValue());
    }
}
