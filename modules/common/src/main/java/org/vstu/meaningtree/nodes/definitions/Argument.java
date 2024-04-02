package org.vstu.meaningtree.nodes.definitions;

import org.vstu.meaningtree.nodes.Type;
import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Identifier;

import java.util.Optional;

public class Argument {
    private final Type _type;
    private final boolean _isListUnpacking;

    public Type getType() {
        return _type;
    }

    public Identifier getName() {
        return _name;
    }

    private final Identifier _name;
    private final Optional<Expression> _initial;

    public Argument(Type type, boolean isListUnpacking, Identifier name, Expression initial) {
        super();
        _type = type;
        _isListUnpacking = isListUnpacking;
        _name = name;
        _initial = Optional.ofNullable(initial);
    }

    public Expression getInitialExpression() {
        if (!hasInitialExpression()) {
            throw new RuntimeException("Initial expression isn't present")
        }
        return _initial.get();
    }

    private boolean hasInitialExpression() {
        return _initial.isPresent();
    }

    public boolean isListUnpacking() {
        return _isListUnpacking;
    }
}
