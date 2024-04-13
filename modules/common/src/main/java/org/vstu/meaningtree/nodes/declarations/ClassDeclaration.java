package org.vstu.meaningtree.nodes.declarations;

import org.vstu.meaningtree.nodes.identifiers.SimpleIdentifier;

public class ClassDeclaration extends Declaration {
    public ClassDeclaration(SimpleIdentifier name) {
        super();
        _name = name;
    }

    protected final SimpleIdentifier _name;

    public SimpleIdentifier getName() {
        return _name;
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
