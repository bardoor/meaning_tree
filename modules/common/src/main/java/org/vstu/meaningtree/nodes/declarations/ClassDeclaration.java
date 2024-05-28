package org.vstu.meaningtree.nodes.declarations;

import org.vstu.meaningtree.nodes.Identifier;

public class ClassDeclaration extends Declaration {
    protected final VisibilityModifier _modifier;
    protected final Identifier _name;

    public ClassDeclaration(VisibilityModifier modifier, Identifier name) {
        _modifier = modifier;
        _name = name;
    }

    public ClassDeclaration(Identifier name) {
        this(VisibilityModifier.NONE, name);
    }

    public VisibilityModifier getVisibilityModifier() {
        return _modifier;
    }

    public Identifier getName() {
        return _name;
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
