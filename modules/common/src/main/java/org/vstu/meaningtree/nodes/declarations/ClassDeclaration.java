package org.vstu.meaningtree.nodes.declarations;

import org.vstu.meaningtree.nodes.Declaration;
import org.vstu.meaningtree.nodes.Type;
import org.vstu.meaningtree.nodes.enums.DeclarationModifier;
import org.vstu.meaningtree.nodes.expressions.Identifier;
import org.vstu.meaningtree.nodes.types.UserType;
import org.vstu.meaningtree.nodes.types.user.Class;
import org.vstu.meaningtree.nodes.types.user.GenericClass;

import java.util.List;

public class ClassDeclaration extends Declaration {
    protected final List<DeclarationModifier> _modifiers;
    protected final Identifier _name;
    protected final List<Type> _parentTypes;
    protected final List<Type> _typeParameters; // for generic type

    public ClassDeclaration(List<DeclarationModifier> modifiers, Identifier name, List<Type> typeParameters, Type ... parents) {
        _modifiers = List.copyOf(modifiers);
        _name = name;
        _typeParameters = List.copyOf(typeParameters);
        _parentTypes = List.of(parents);
    }

    public ClassDeclaration(List<DeclarationModifier> modifiers, Identifier name, Type ... parents) {
        this(modifiers, name, List.of(), parents);
    }

    public ClassDeclaration(Identifier name, List<Type> typeParameters) {
        this(List.of(), name, typeParameters);
    }

    public ClassDeclaration(Identifier name) {
        this(List.of(), name);
    }

    public List<DeclarationModifier> getModifiers() {
        return _modifiers;
    }

    public List<Type> getParents() {
        return _parentTypes;
    }

    public Identifier getName() {
        return _name;
    }

    public List<Type> getTypeParameters() {
        return _typeParameters;
    }

    public boolean isGeneric() {
        return !_typeParameters.isEmpty();
    }

    public UserType getTypeNode() {
        if (isGeneric()) {
            return new GenericClass(_name, getTypeParameters().toArray(new Type[0]));
        }
        return new Class(_name);
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
