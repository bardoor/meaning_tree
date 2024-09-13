package org.vstu.meaningtree.nodes.modules;

import org.vstu.meaningtree.nodes.expressions.Identifier;
import org.vstu.meaningtree.nodes.Node;

public abstract class Import extends Node {
    private final Identifier _scope;

    public Import(Identifier scope) {
        _scope = scope;
    }

    public Identifier getScope() {
        return _scope;
    }

    @Override
    public String generateDot() {
        return "";
    }
}
