package org.vstu.meaningtree.nodes.declarations;

import org.vstu.meaningtree.nodes.Type;
import org.vstu.meaningtree.nodes.Identifier;

public class VariableDeclaration extends Declaration {
    protected final Type _type;

    public VariableDeclaration(Type type, Identifier name) {
        super(name);
        _type = type;
    }

    public Type getType() {
        return _type;
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
