package org.vstu.meaningtree.nodes;

import org.vstu.meaningtree.nodes.expressions.identifiers.SimpleIdentifier;

abstract public class Expression extends Node {
    @Override
    public Expression clone() {
        return (Expression) super.clone();
    }

    public boolean equalsIdentifier(String name) {
        return equals(new SimpleIdentifier(name));
    }
}
