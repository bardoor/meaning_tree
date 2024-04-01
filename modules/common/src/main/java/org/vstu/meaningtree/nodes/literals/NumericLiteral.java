package org.vstu.meaningtree.nodes.literals;

import org.vstu.meaningtree.nodes.Literal;

abstract public class NumericLiteral extends Literal {
    abstract public Number getValue();

    @Override
    public String generateDot() {
        return getValue().toString() + "\n";
    }
}
