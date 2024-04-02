package org.vstu.meaningtree.nodes.definitions;

import org.vstu.meaningtree.nodes.Type;
import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Identifier;
import org.vstu.meaningtree.nodes.CanInitialize;
import org.vstu.meaningtree.nodes.declarations.VariableDeclaration;

public class VariableDefinition extends Definition implements CanInitialize {
    protected final Expression _rvalue;

    public VariableDefinition(Type type, Identifier name, Expression rvalue) {
        super(new VariableDeclaration(type, name));
        _rvalue = rvalue;
    }

    public Type getType() {
        return ((VariableDeclaration)getDeclaration()).getType();
    }

    public Identifier getName() {
        return ((VariableDeclaration)getDeclaration()).getName();
    }

    public Expression getRValue() {
        return _rvalue;
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
