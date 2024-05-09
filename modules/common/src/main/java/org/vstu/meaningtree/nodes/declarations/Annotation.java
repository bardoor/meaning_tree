package org.vstu.meaningtree.nodes.declarations;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Identifier;
import org.vstu.meaningtree.nodes.definitions.DefinitionArgument;
import org.vstu.meaningtree.nodes.identifiers.SimpleIdentifier;

import java.util.List;

public class Annotation extends Declaration {
    private final List<Expression> arguments;
    private final Identifier _name;

    public Annotation(Identifier name, Expression... arguments) {
        _name = name;
        this.arguments = List.of(arguments);
    }

    public Expression[] getArguments() {
        return arguments.toArray(new Expression[0]);
    }

    public Identifier getName() {
        return _name;
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
