package org.vstu.meaningtree.nodes;

public class Identifier extends Expression {
    protected final String name;

    public Identifier(String name) {
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
