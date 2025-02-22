package org.vstu.meaningtree.nodes.definitions.components;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.expressions.identifiers.SimpleIdentifier;

import java.util.Objects;
public class DefinitionArgument extends Expression {
    public SimpleIdentifier getName() {
        return _name;
    }

    private SimpleIdentifier _name;
    private Expression _initial;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DefinitionArgument that = (DefinitionArgument) o;
        return Objects.equals(_name, that._name) && Objects.equals(_initial, that._initial);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), _name, _initial);
    }

    public DefinitionArgument(SimpleIdentifier name, Expression initial) {
        super();
        _name = name;
        _initial = Objects.requireNonNull(initial);
    }

    public Expression getInitialExpression() {
        return _initial;
    }

    @Override
    public DefinitionArgument clone() {
        DefinitionArgument obj = (DefinitionArgument) super.clone();
        obj._name = _name.clone();
        obj._initial = _initial.clone();
        return obj;
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
