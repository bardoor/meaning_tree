package org.vstu.meaningtree.nodes.expressions.other;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.statements.DeleteStatement;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DeleteExpression that = (DeleteExpression) o;
        return _isCollection == that._isCollection && Objects.equals(_target, that._target);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), _target, _isCollection);
    }
}
