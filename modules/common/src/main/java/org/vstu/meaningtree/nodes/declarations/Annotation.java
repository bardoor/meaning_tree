package org.vstu.meaningtree.nodes.declarations;

import org.vstu.meaningtree.nodes.identifiers.SimpleIdentifier;
import org.vstu.meaningtree.nodes.definitions.Argument;

import java.util.List;

public class Annotation extends Declaration {
    private final List<Argument> arguments;
    private final SimpleIdentifier _name;

    public Annotation(SimpleIdentifier name, Argument ... arguments) {
        _name = name;
        this.arguments = List.of(arguments);
    }

    public SimpleIdentifier getName() {
        return _name;
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
