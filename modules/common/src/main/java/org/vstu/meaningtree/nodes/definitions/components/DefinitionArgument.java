package org.vstu.meaningtree.nodes.definitions.components;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.expressions.identifiers.SimpleIdentifier;
import org.vstu.meaningtree.utils.TreeNode;

import java.util.Objects;
public class DefinitionArgument extends Expression {
    public SimpleIdentifier getName() {
        return name;
    }

    @TreeNode private SimpleIdentifier name;
    @TreeNode private Expression initial;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DefinitionArgument that = (DefinitionArgument) o;
        return Objects.equals(name, that.name) && Objects.equals(initial, that.initial);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, initial);
    }

    public DefinitionArgument(SimpleIdentifier name, Expression initial) {
        super();
        this.name = name;
        this.initial = Objects.requireNonNull(initial);
    }

    public Expression getInitialExpression() {
        return initial;
    }

    @Override
    public DefinitionArgument clone() {
        DefinitionArgument obj = (DefinitionArgument) super.clone();
        obj.name = name.clone();
        obj.initial = initial.clone();
        return obj;
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
