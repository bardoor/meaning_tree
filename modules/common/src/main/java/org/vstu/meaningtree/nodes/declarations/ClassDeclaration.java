package org.vstu.meaningtree.nodes.declarations;

import org.vstu.meaningtree.nodes.Identifier;
import org.vstu.meaningtree.nodes.Type;

import java.util.List;

public class ClassDeclaration extends Declaration {
    protected final VisibilityModifier _modifier;
    protected final Identifier _name;
    protected final List<Type> _parentTypes;

    public ClassDeclaration(VisibilityModifier modifier, Identifier name, Type ... parents) {
        _modifier = modifier;
        _name = name;
        _parentTypes = List.of(parents);
    }

    public ClassDeclaration(Identifier name) {
        this(VisibilityModifier.NONE, name);
    }

    public VisibilityModifier getVisibilityModifier() {
        return _modifier;
    }

    public List<Type> getParents() {
        return _parentTypes;
    }

    public Identifier getName() {
        return _name;
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
