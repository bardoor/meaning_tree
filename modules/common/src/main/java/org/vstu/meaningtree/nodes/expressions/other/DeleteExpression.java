package org.vstu.meaningtree.nodes.expressions.other;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.statements.DeleteStatement;

public class DeleteExpression extends Expression {
    private final Expression _target;
    private final boolean _isCollection;

    public DeleteExpression(Expression target, boolean isCollection) {
        _target = target;
        _isCollection = isCollection;
    }

    public DeleteExpression(Expression target) {
        this(target, false);
    }

    public Expression getTarget() {
        return _target;
    }

    public DeleteStatement toStatement() {
        return new DeleteStatement(_target, _isCollection);
    }

    public boolean isCollectionTarget() {
        return _isCollection;
    }
}
