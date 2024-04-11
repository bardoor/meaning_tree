package org.vstu.meaningtree.nodes.declarations;

import org.vstu.meaningtree.nodes.Expression;
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

    public FieldDeclaration(Type type, Identifier name, Expression value, VisibilityModifier modifier, boolean isStatic) {
        super(type, name, value);
        this.modifier = modifier;
        this.isStatic = isStatic;
    }


    public FieldDeclaration(Type type, VisibilityModifier modifier, boolean isStatic, VariableDeclarator... fields) {
        super(type, fields);
        this.modifier = modifier;
        this.isStatic = isStatic;
    }

    public FieldDeclaration(Type type, Identifier name) {
        this(type, name, VisibilityModifier.NONE, false);
    }


    public FieldDeclaration(Type type, Identifier name, Expression value) {
        this(type, name, value, VisibilityModifier.NONE, false);
    }

    public FieldDeclaration(Type type, VariableDeclarator... fields) {
        this(type, VisibilityModifier.NONE, false, fields);
    }

    public VisibilityModifier getVisibilityModifier() {
        return modifier;
    }

    public boolean isStatic() {
        return isStatic;
    }
}
