package org.vstu.meaningtree.nodes.declarations;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.identifiers.SimpleIdentifier;

import java.util.Optional;

public class VariableDeclarator {
    private final SimpleIdentifier _identifier;
    private final Optional<Expression> _rvalue;

    public VariableDeclarator(SimpleIdentifier identifier, Expression rvalue) {
        _identifier = identifier;
        _rvalue = Optional.ofNullable(rvalue);
    }

    public VariableDeclarator(SimpleIdentifier identifier) {
        this(identifier, null);
    }

    public Expression getRValue() {
        if (!hasInitialization()) {
            throw new RuntimeException("Initializer is not present");
        }
        return _rvalue.get();
    }

    public boolean hasInitialization() {
        return _rvalue.isPresent();
    }

    public SimpleIdentifier getIdentifier() {
        return _identifier;
    }
}
