package org.vstu.meaningtree.nodes.comprehensions;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.declarations.VariableDeclaration;

public class ContainerBasedComprehension extends Comprehension {
    private final VariableDeclaration _containerItem;
    private final Expression _container;

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
