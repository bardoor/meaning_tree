package org.vstu.meaningtree.nodes.declarations;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Type;
import org.vstu.meaningtree.nodes.identifiers.SimpleIdentifier;

import java.util.List;

public class FieldDeclaration extends VariableDeclaration {
    private final List<Modifier> _modifiers;

    public FieldDeclaration(Type type, SimpleIdentifier name, List<Modifier> modifiers) {
        super(type, name);
        _modifiers = List.copyOf(modifiers);
    }

    public FieldDeclaration(Type type, SimpleIdentifier name, Expression value, List<Modifier> modifiers) {
        super(type, name, value);
        _modifiers = List.copyOf(modifiers);
    }


    public FieldDeclaration(Type type, List<Modifier> modifiers, VariableDeclarator... fields) {
        super(type, fields);
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

    public List<Modifier> getModifiers() {
        return _modifiers;
    }
}
