package org.vstu.meaningtree.nodes.declarations;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Node;
import org.vstu.meaningtree.nodes.identifiers.SimpleIdentifier;

import java.util.Optional;

public class VariableDeclarator extends Node {
    private final SimpleIdentifier _identifier;
    private final Expression _rvalue;

    public VariableDeclarator(SimpleIdentifier identifier, Expression rvalue) {
        _identifier = identifier;
        _rvalue = rvalue;
    }

    public VariableDeclarator(SimpleIdentifier identifier) {
        this(identifier, null);
    }

    public Optional<Expression> getRValue() {
        return Optional.ofNullable(_rvalue);
    }

    public boolean hasInitialization() {
        return _rvalue != null;
    }

    public SimpleIdentifier getIdentifier() {
        return _identifier;
    }
}
