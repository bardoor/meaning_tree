package org.vstu.meaningtree.nodes.declarations.components;

import org.jetbrains.annotations.Nullable;
import org.vstu.meaningtree.iterators.utils.TreeNode;
import org.vstu.meaningtree.nodes.Declaration;
import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Type;
import org.vstu.meaningtree.nodes.expressions.identifiers.SimpleIdentifier;

import java.util.Objects;

public class DeclarationArgument extends Declaration {
    @TreeNode private Type type;
    @TreeNode private boolean isListUnpacking;
    @TreeNode private SimpleIdentifier name;
    @TreeNode @Nullable private Expression initial;

    public Type getType() {
        return type;
    }

    public SimpleIdentifier getName() {
        return name;
    }

    public DeclarationArgument(Type type, boolean isListUnpacking, SimpleIdentifier name, @Nullable Expression initial) {
        this.type = type;
        this.isListUnpacking = isListUnpacking;
        this.name = name;
        this.initial = initial;
    }

    public Expression getInitialExpression() {
        return Objects.requireNonNull(initial, "Initial expression isn't present");
    }

    public boolean hasInitialExpression() {
        return initial != null;
    }

    public boolean isListUnpacking() {
        return isListUnpacking;
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DeclarationArgument that = (DeclarationArgument) o;
        return isListUnpacking == that.isListUnpacking && Objects.equals(type, that.type) && Objects.equals(name, that.name) && Objects.equals(initial, that.initial);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), type, isListUnpacking, name, initial);
    }
}
