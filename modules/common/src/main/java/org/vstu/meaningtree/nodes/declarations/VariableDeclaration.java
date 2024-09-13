package org.vstu.meaningtree.nodes.declarations;

import org.vstu.meaningtree.enums.DeclarationModifier;
import org.vstu.meaningtree.nodes.Declaration;
import org.vstu.meaningtree.nodes.declarations.components.VariableDeclarator;
import org.vstu.meaningtree.nodes.interfaces.HasInitialization;
import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Type;
import org.vstu.meaningtree.nodes.expressions.identifiers.SimpleIdentifier;

import java.util.ArrayList;
import java.util.List;

public class VariableDeclaration extends Declaration implements HasInitialization {
    protected final Type _type;
    protected final List<VariableDeclarator> variableDeclaratorList;

    public VariableDeclaration(Type type, SimpleIdentifier name) {
        this(type, name, null);
    }

    public VariableDeclaration(Type type, SimpleIdentifier name, Expression value) {
        variableDeclaratorList = new ArrayList<>();
        variableDeclaratorList.add(new VariableDeclarator(name, value));
        _type = type;
    }

    public VariableDeclaration(Type type, VariableDeclarator... variableDeclarators) {
        this(type, List.of(variableDeclarators));
    }

    public VariableDeclaration(Type type, List<VariableDeclarator> variableDeclarators) {
        variableDeclaratorList = List.copyOf(variableDeclarators);
        _type = type;
    }

    public Type getType() {
        return _type;
    }

    public FieldDeclaration makeField(List<DeclarationModifier> modifiers) {
        return new FieldDeclaration(getType(), modifiers, getDeclarators());
    }

    public VariableDeclarator[] getDeclarators() {
        return variableDeclaratorList.toArray(new VariableDeclarator[0]);
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
