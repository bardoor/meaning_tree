package org.vstu.meaningtree.nodes;

abstract public class Expression extends Node {
    @Override
    public Expression clone() {
        return (Expression) super.clone();
    }
}
