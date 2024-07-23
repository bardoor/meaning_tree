package org.vstu.meaningtree.nodes.declarations;

import org.vstu.meaningtree.nodes.identifiers.Identifier;
import org.vstu.meaningtree.nodes.Type;

import java.util.List;

public class ClassDeclaration extends Declaration {
    protected final List<Modifier> _modifiers;
    protected final Identifier _name;
    protected final List<Type> _parentTypes;

    public ClassDeclaration(List<Modifier> modifiers, Identifier name, Type ... parents) {
        _modifiers = List.copyOf(modifiers);
        _name = name;
        _parentTypes = List.of(parents);
    }

    public ClassDeclaration(Identifier name) {
        this(List.of(), name);
    }

    public List<Modifier> getModifiers() {
        return _modifiers;
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
