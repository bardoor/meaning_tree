package org.vstu.meaningtree.nodes.declarations;

import org.jetbrains.annotations.NotNull;
import org.vstu.meaningtree.nodes.Declaration;
import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Type;
import org.vstu.meaningtree.nodes.declarations.components.VariableDeclarator;
import org.vstu.meaningtree.nodes.enums.DeclarationModifier;
import org.vstu.meaningtree.nodes.expressions.identifiers.SimpleIdentifier;
import org.vstu.meaningtree.nodes.interfaces.HasInitialization;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class VariableDeclaration extends Declaration implements HasInitialization, Iterable<VariableDeclarator> {
    protected Type _type;
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

    public void setType(Type newType) {
        _type = newType;
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

    @NotNull
    @Override
    public Iterator<VariableDeclarator> iterator() {
        return variableDeclaratorList.iterator();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        VariableDeclaration that = (VariableDeclaration) o;
        return Objects.equals(_type, that._type) && Objects.equals(variableDeclaratorList, that.variableDeclaratorList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), _type, variableDeclaratorList);
    }
}
