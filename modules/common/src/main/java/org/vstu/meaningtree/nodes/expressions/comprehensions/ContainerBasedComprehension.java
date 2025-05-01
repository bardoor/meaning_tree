package org.vstu.meaningtree.nodes.expressions.comprehensions;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.declarations.VariableDeclaration;
import org.vstu.meaningtree.utils.TreeNode;

import java.util.Objects;

public class ContainerBasedComprehension extends Comprehension {
    @TreeNode private VariableDeclaration containerItem;
    @TreeNode private Expression container;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ContainerBasedComprehension that = (ContainerBasedComprehension) o;
        return Objects.equals(containerItem, that.containerItem) && Objects.equals(container, that.container);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), containerItem, container);
    }

    public ContainerBasedComprehension(ComprehensionItem compItem, VariableDeclaration containerItem, Expression container, Expression condition) {
        super(compItem, condition);
        this.containerItem = containerItem;
        this.container = container;
    }

    public VariableDeclaration getContainerItemDeclaration() {
        return containerItem;
    }

    public Expression getContainerExpression() {
        return container;
    }

    public ContainerBasedComprehension clone() {
        ContainerBasedComprehension obj = (ContainerBasedComprehension) super.clone();
        obj.containerItem = containerItem;
        obj.container = container;
        return obj;
    }
}
