package org.vstu.meaningtree.nodes.identifiers;

import org.vstu.meaningtree.nodes.Identifier;

public class SimpleIdentifier extends Identifier {
    protected final String name;

    public SimpleIdentifier(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public String generateDot() {
        return String.format("%s [label=\"%s\"];\n", _id, getName());
    }
}
