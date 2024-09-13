package org.vstu.meaningtree.nodes.declarations;

import org.vstu.meaningtree.enums.DeclarationModifier;
import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Type;
import org.vstu.meaningtree.nodes.declarations.components.VariableDeclarator;
import org.vstu.meaningtree.nodes.expressions.identifiers.SimpleIdentifier;

import java.util.List;

public class FieldDeclaration extends VariableDeclaration {
    private final List<DeclarationModifier> _modifiers;

    public FieldDeclaration(Type type, SimpleIdentifier name, List<DeclarationModifier> modifiers) {
        super(type, name);
        _modifiers = List.copyOf(modifiers);
    }

    public FieldDeclaration(Type type, SimpleIdentifier name, Expression value, List<DeclarationModifier> modifiers) {
        super(type, name, value);
        _modifiers = List.copyOf(modifiers);
    }


    public FieldDeclaration(Type type, List<DeclarationModifier> modifiers, VariableDeclarator... declarators) {
        this(type, modifiers, List.of(declarators));
    }

    public FieldDeclaration(Type type, List<DeclarationModifier> modifiers, List<VariableDeclarator> declarators) {
        super(type, declarators);
        _modifiers = List.copyOf(modifiers);
    }

    public FieldDeclaration(Type type, SimpleIdentifier name) {
        this(type, name, List.of());
    }


    public FieldDeclaration(Type type, SimpleIdentifier name, Expression value) {
        this(type, name, value, List.of());
    }

    public FieldDeclaration(Type type, VariableDeclarator... fields) {
        this(type, List.of(), fields);
    }

    public List<DeclarationModifier> getModifiers() {
        return _modifiers;
    }
}
