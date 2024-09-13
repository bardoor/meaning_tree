package org.vstu.meaningtree.nodes.expressions.literals;

import org.vstu.meaningtree.nodes.expressions.Literal;

abstract public class NumericLiteral extends Literal {
    abstract public Number getValue();
    abstract public String getStringValue();

    @Override
    public String generateDot() {
        return String.format("%s [label=\"%s\"];\n", _id, getValue());
    }
}
