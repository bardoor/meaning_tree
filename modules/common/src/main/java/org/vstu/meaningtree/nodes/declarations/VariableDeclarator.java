package org.vstu.meaningtree.nodes.declarations;

import org.jetbrains.annotations.Nullable;
import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Node;
import org.vstu.meaningtree.nodes.identifiers.SimpleIdentifier;

import java.util.Optional;

public class VariableDeclarator extends Node {
    private final SimpleIdentifier _identifier;

    @Nullable
    private final Expression _rvalue;

    public VariableDeclarator(SimpleIdentifier identifier, @Nullable Expression rvalue) {
        _identifier = identifier;
        _rvalue = rvalue;
    }

    public VariableDeclarator(SimpleIdentifier identifier) {
        this(identifier, null);
    }

    @Nullable
    public Expression getRValue() {
        return _rvalue;
    }

    public boolean hasInitialization() {
        return _rvalue != null;
    }

    public SimpleIdentifier getIdentifier() {
        return _identifier;
    }
}
