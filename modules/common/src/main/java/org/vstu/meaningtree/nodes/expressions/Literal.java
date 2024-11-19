package org.vstu.meaningtree.nodes.expressions;

import org.vstu.meaningtree.nodes.Expression;

abstract public class Literal extends Expression {
    @Override
    public Literal clone() {
        return (Literal) super.clone();
    }
}
