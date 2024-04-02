package org.vstu.meaningtree.nodes.declarations;

import org.vstu.meaningtree.nodes.Identifier;
import org.vstu.meaningtree.nodes.Node;

public abstract class Declaration extends Node {
    protected final Identifier _name;

    public Declaration(Identifier name) {
        _name = name;
    }

    public Identifier getName() {
        return _name;
    }

}
