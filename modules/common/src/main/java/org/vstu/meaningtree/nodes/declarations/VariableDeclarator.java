package org.vstu.meaningtree.nodes.declarations;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Identifier;

import java.util.Optional;

public class VariableDeclarator {
    private final Identifier _identifier;
    private final Optional<Expression> _rvalue;

    public VariableDeclarator(Identifier identifier, Expression rvalue) {
        _identifier = identifier;
        _rvalue = Optional.ofNullable(rvalue);
    }

    public VariableDeclarator(Identifier identifier) {
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

    public Identifier getIdentifier() {
        return _identifier;
    }
}
