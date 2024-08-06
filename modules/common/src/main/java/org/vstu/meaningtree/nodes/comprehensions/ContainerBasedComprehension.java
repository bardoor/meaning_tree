package org.vstu.meaningtree.nodes.comprehensions;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.declarations.VariableDeclaration;

import java.util.Objects;

public class ContainerBasedComprehension extends Comprehension {
    private final VariableDeclaration _containerItem;
    private final Expression _container;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ContainerBasedComprehension that = (ContainerBasedComprehension) o;
        return Objects.equals(_containerItem, that._containerItem) && Objects.equals(_container, that._container);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), _containerItem, _container);
    }

    public ContainerBasedComprehension(ComprehensionItem compItem, VariableDeclaration containerItem, Expression container, Expression condition) {
        super(compItem, condition);
        this._containerItem = containerItem;
        this._container = container;
    }

    public VariableDeclaration getContainerItemDeclaration() {
        return _containerItem;
    }

    public Expression getContainerExpression() {
        return _container;
    }
}
