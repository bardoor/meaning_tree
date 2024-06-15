package org.vstu.meaningtree.nodes.declarations;

import org.vstu.meaningtree.nodes.Identifier;
import org.vstu.meaningtree.nodes.Type;
import org.vstu.meaningtree.nodes.types.UserType;

public class MethodDeclaration extends FunctionDeclaration {
    private final UserType _owner;
    private final VisibilityModifier modifier;
    private final boolean isStatic;

    public MethodDeclaration(UserType owner, Identifier name, Type returnType, Annotation annotation, VisibilityModifier modifier, boolean isStatic, DeclarationArgument... arguments) {
        super(name, returnType, annotation, arguments);
        _owner = owner;
        this.modifier = modifier;
        this.isStatic = isStatic;
    }

    public UserType getOwner() {
        return _owner;
    }

    public VisibilityModifier getVisibilityModifier() {
        return modifier;
    }

    public boolean isStatic() {
        return isStatic;
    }
}
