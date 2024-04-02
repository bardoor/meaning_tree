package org.vstu.meaningtree.nodes.declarations;

import org.vstu.meaningtree.nodes.Type;
import org.vstu.meaningtree.nodes.Identifier;

public class FieldDeclaration extends VariableDeclaration {
    private final VisibilityModifier modifier;
    private final boolean isStatic;

    public FieldDeclaration(Type type, Identifier name, VisibilityModifier modifier, boolean isStatic) {
        super(type, name);
        this.modifier = modifier;
        this.isStatic = isStatic;
    }

    public FieldDeclaration(Type type, Identifier name) {
        super(type, name);
        this.modifier = VisibilityModifier.DEFAULT;
        this.isStatic = false;
    }
}
