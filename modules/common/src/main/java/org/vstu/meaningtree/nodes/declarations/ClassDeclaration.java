package org.vstu.meaningtree.nodes.declarations;

import org.vstu.meaningtree.nodes.Identifier;

public class ClassDeclaration extends Declaration {
    public ClassDeclaration(Identifier name) {
        super(name);
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
