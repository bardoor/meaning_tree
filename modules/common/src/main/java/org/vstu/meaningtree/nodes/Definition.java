package org.vstu.meaningtree.nodes;

import org.vstu.meaningtree.utils.TreeNode;

public abstract class Definition extends Node {
    @TreeNode private Declaration _decl;

    protected Definition(Declaration decl) {
        _decl = decl;
    }

    public Declaration getDeclaration() {
        return _decl;
    }
}
