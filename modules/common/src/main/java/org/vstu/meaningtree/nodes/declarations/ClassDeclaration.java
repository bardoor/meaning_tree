package org.vstu.meaningtree.nodes.declarations;

import org.vstu.meaningtree.nodes.Declaration;
import org.vstu.meaningtree.nodes.Type;
import org.vstu.meaningtree.nodes.enums.DeclarationModifier;
import org.vstu.meaningtree.nodes.expressions.Identifier;
import org.vstu.meaningtree.nodes.types.UserType;
import org.vstu.meaningtree.nodes.types.user.Class;
import org.vstu.meaningtree.nodes.types.user.GenericClass;
import org.vstu.meaningtree.utils.TreeNode;

import java.util.List;

public class ClassDeclaration extends Declaration {
    protected List<DeclarationModifier> modifiers;
    @TreeNode protected Identifier name;
    @TreeNode protected List<Type> parentTypes;
    @TreeNode protected List<Type> typeParameters; // for generic type

    public ClassDeclaration(List<DeclarationModifier> modifiers, Identifier name, List<Type> typeParameters, Type ... parents) {
        this.modifiers = List.copyOf(modifiers);
        this.name = name;
        this.typeParameters = List.copyOf(typeParameters);
        parentTypes = List.of(parents);
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
        return modifiers;
    }

    public List<Type> getParents() {
        return parentTypes;
    }

    public Identifier getName() {
        return name;
    }

    public List<Type> getTypeParameters() {
        return typeParameters;
    }

    public boolean isGeneric() {
        return !typeParameters.isEmpty();
    }

    public UserType getTypeNode() {
        if (isGeneric()) {
            return new GenericClass(name, getTypeParameters().toArray(new Type[0]));
        }
        return new Class(name);
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
