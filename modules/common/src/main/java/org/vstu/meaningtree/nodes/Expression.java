package org.vstu.meaningtree.nodes;

import org.vstu.meaningtree.nodes.expressions.identifiers.SimpleIdentifier;
import org.vstu.meaningtree.nodes.expressions.logical.NotOp;

abstract public class Expression extends Node {
    @Override
    public Expression clone() {
        return (Expression) super.clone();
    }

    public boolean equalsIdentifier(String name) {
        return equals(new SimpleIdentifier(name));
    }

    public boolean evaluatesToBoolean() {
        return false;
    }

    public Expression tryInvert() {
        if (evaluatesToBoolean()) {
            return new NotOp(this);
        }
        return this;
    }
}
