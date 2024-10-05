package org.vstu.meaningtree.nodes.statements;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Statement;
import org.vstu.meaningtree.nodes.expressions.other.DeleteExpression;

public class DeleteStatement extends Statement {
    private final Expression _target;
    private final boolean _isCollection;

    public DeleteStatement(Expression target, boolean isCollection) {
        _target = target;
        _isCollection = isCollection;
    }

    public DeleteStatement(Expression target) {
        this(target, false);
    }

    public Expression getTarget() {
        return _target;
    }

    public DeleteExpression toExpression() {
        return new DeleteExpression(_target, _isCollection);
    }

    public boolean isCollectionTarget() {
        return _isCollection;
    }
}
