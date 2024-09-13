package org.vstu.meaningtree.nodes;

public abstract class Definition extends Node {
    private final Declaration _decl;


    protected Definition(Declaration decl) {
        _decl = decl;
    }

    public Declaration getDeclaration() {
        return _decl;
    }
}
