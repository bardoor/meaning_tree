package org.vstu.meaningtree.nodes.declarations;

import org.vstu.meaningtree.nodes.Type;
import org.vstu.meaningtree.nodes.definitions.Argument;
import org.vstu.meaningtree.nodes.Identifier;

public class MethodDeclaration extends FunctionDeclaration {
    private final Type _owner;
    private final VisibilityModifier modifier;
    private final boolean isStatic;

    public MethodDeclaration(Type owner, Identifier name, Type returnType, Annotation annotation, VisibilityModifier modifier, boolean isStatic, Argument... arguments) {
        super(name, returnType, annotation, arguments);
        _owner = owner;
        this.modifier = modifier;
        this.isStatic = isStatic;
    }

    public VisibilityModifier getVisibilityModifier() {
        return modifier;
    }

    public boolean isStatic() {
        return isStatic;
    }
}
