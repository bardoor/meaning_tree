package org.vstu.meaningtree.nodes.expressions;

import org.vstu.meaningtree.nodes.Expression;

public abstract class Identifier extends Expression {
    @Override
    public Identifier clone() {
        return (Identifier) super.clone();
    }
}
