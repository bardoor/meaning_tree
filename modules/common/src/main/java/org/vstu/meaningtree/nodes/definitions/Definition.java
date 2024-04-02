package org.vstu.meaningtree.nodes.definitions;

import org.vstu.meaningtree.nodes.Node;
import org.vstu.meaningtree.nodes.declarations.Declaration;

public abstract class Definition extends Node {
    private final Declaration _decl;


    protected Definition(Declaration decl) {
        _decl = decl;
    }

    public Declaration getDeclaration() {
        return _decl;
    }
}
