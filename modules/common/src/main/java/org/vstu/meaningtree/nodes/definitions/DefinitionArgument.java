package org.vstu.meaningtree.nodes.definitions;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.identifiers.SimpleIdentifier;

import java.util.Objects;
public class DefinitionArgument extends Expression {
    public SimpleIdentifier getName() {
        return _name;
    }

    private final SimpleIdentifier _name;
    private final Expression _initial;

    public DefinitionArgument(SimpleIdentifier name, Expression initial) {
        super();
        _name = name;
        _initial = Objects.requireNonNull(initial);
    }

    public Expression getInitialExpression() {
        return _initial;
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
