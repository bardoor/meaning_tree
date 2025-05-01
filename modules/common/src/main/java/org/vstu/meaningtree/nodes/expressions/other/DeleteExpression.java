package org.vstu.meaningtree.nodes.expressions.other;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.statements.DeleteStatement;
import org.vstu.meaningtree.utils.TreeNode;

import java.util.Objects;

public class DeleteExpression extends Expression {
    @TreeNode private Expression target;
    @TreeNode private final boolean isCollection;

    public DeleteExpression(Expression target, boolean isCollection) {
        this.target = target;
        this.isCollection = isCollection;
    }

    public DeleteExpression(Expression target) {
        this(target, false);
    }

    public Expression getTarget() {
        return target;
    }

    public DeleteStatement toStatement() {
        return new DeleteStatement(target, isCollection);
    }

    public boolean isCollectionTarget() {
        return isCollection;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DeleteExpression that = (DeleteExpression) o;
        return isCollection == that.isCollection && Objects.equals(target, that.target);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), target, isCollection);
    }

    @Override
    public DeleteExpression clone() {
        DeleteExpression obj = (DeleteExpression) super.clone();
        obj.target = target.clone();
        return obj;
    }
}
