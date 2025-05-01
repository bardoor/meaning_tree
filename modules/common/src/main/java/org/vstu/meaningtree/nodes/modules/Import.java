package org.vstu.meaningtree.nodes.modules;

import org.vstu.meaningtree.nodes.Node;
import org.vstu.meaningtree.nodes.expressions.Identifier;
import org.vstu.meaningtree.utils.TreeNode;

public abstract class Import extends Node {
    @TreeNode private Identifier scope;

    public Import(Identifier scope) {
        this.scope = scope;
    }

    public Identifier getScope() {
        return scope;
    }

    @Override
    public String generateDot() {
        return "";
    }
}
