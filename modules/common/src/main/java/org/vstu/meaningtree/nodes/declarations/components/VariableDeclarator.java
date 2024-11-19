package org.vstu.meaningtree.nodes.declarations.components;

import org.jetbrains.annotations.Nullable;
import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Node;
import org.vstu.meaningtree.nodes.expressions.identifiers.SimpleIdentifier;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        VariableDeclarator that = (VariableDeclarator) o;
        return Objects.equals(_identifier, that._identifier) && Objects.equals(_rvalue, that._rvalue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), _identifier, _rvalue);
    }
}
