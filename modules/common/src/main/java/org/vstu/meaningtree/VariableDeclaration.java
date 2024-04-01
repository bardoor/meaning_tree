package org.vstu.meaningtree;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Identifier;

public class VariableDeclaration {
    protected final Type type;
    protected final Identifier name;
    protected final Expression rvalue;

    public VariableDeclaration(Type type, Identifier name, Expression rvalue) {
        this.type = type;
        this.name = name;
        this.rvalue = rvalue;
    }

    public Type getType() {
        return type;
    }

    public Identifier getName() {
        return name;
    }

    public Expression getRValue() {
        return rvalue;
    }
}
