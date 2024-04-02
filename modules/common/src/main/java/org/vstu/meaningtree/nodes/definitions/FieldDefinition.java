package org.vstu.meaningtree.nodes.definitions;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.declarations.FieldDeclaration;

public class FieldDefinition extends Definition {
    private Expression _rvalue;

    protected FieldDefinition(FieldDeclaration decl, Expression expr) {
        super(decl);
        _rvalue = expr;
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }

    public Expression getRValue() {
        return _rvalue;
    }
}
