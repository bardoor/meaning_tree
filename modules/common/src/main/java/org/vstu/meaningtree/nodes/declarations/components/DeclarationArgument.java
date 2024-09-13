package org.vstu.meaningtree.nodes.declarations.components;

import org.jetbrains.annotations.Nullable;
import org.vstu.meaningtree.nodes.Declaration;
import org.vstu.meaningtree.nodes.Type;
import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.expressions.identifiers.SimpleIdentifier;

import java.util.Objects;

public class DeclarationArgument extends Declaration {
    private final Type _type;
    private final boolean _isListUnpacking;

    public Type getType() {
        return _type;
    }

    public SimpleIdentifier getName() {
        return _name;
    }

    private final SimpleIdentifier _name;

    @Nullable
    private final Expression _initial;

    public DeclarationArgument(Type type, boolean isListUnpacking, SimpleIdentifier name, @Nullable Expression initial) {
        _type = type;
        _isListUnpacking = isListUnpacking;
        _name = name;
        _initial = initial;
    }

    public Expression getInitialExpression() {
        return Objects.requireNonNull(_initial, "Initial expression isn't present");
    }

    public boolean hasInitialExpression() {
        return _initial != null;
    }

    public boolean isListUnpacking() {
        return _isListUnpacking;
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
