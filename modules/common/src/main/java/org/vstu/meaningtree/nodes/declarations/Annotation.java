package org.vstu.meaningtree.nodes.declarations;

import org.vstu.meaningtree.nodes.Identifier;
import org.vstu.meaningtree.nodes.definitions.Argument;

import java.util.List;

public class Annotation extends Declaration {
    private final List<Argument> arguments;

    public Annotation(Identifier name, Argument ... arguments) {
        super(name);
        this.arguments = List.of(arguments);
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
