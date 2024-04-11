package org.vstu.meaningtree.nodes.declarations;

import org.vstu.meaningtree.nodes.Identifier;
import org.vstu.meaningtree.nodes.definitions.Argument;

import java.util.List;

public class Annotation extends Declaration {
    private final List<Argument> arguments;
    private final Identifier _name;

    public Annotation(Identifier name, Argument ... arguments) {
        _name = name;
        this.arguments = List.of(arguments);
    }

    public Identifier getName() {
        return _name;
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
