package org.vstu.meaningtree.nodes.definitions;

import org.vstu.meaningtree.nodes.Type;
import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.identifiers.SimpleIdentifier;

import java.util.Optional;

public class Argument {
    private final Type _type;
    private final boolean _isListUnpacking;

    public Type getType() {
        return _type;
    }

    public SimpleIdentifier getName() {
        return _name;
    }

    private final SimpleIdentifier _name;
    private final Optional<Expression> _initial;

    public Argument(Type type, boolean isListUnpacking, SimpleIdentifier name, Expression initial) {
        super();
        _type = type;
        _isListUnpacking = isListUnpacking;
        _name = name;
        _initial = Optional.ofNullable(initial);
    }

    public Expression getInitialExpression() {
        if (!hasInitialExpression()) {
            throw new RuntimeException("Initial expression isn't present");
        }
        return _initial.get();
    }

    public boolean hasInitialExpression() {
        return _initial.isPresent();
    }

    public boolean isListUnpacking() {
        return _isListUnpacking;
    }
}
