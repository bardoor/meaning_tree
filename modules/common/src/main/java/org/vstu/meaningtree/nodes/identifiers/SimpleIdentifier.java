package org.vstu.meaningtree.nodes.identifiers;

import org.vstu.meaningtree.nodes.Expression;

public class SimpleIdentifier extends Expression {
    protected final String name;

    public SimpleIdentifier(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String generateDot() {
        return String.format("%s [label=\"%s\"];\n", _id, getName());
    }
}
