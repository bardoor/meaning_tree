package org.vstu.meaningtree.nodes.statements;

import org.vstu.meaningtree.iterators.utils.TreeNode;
import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Statement;
import org.vstu.meaningtree.nodes.expressions.other.DeleteExpression;

public class DeleteStatement extends Statement {
    @TreeNode private Expression target;
    private boolean isCollection;

    public DeleteStatement(Expression target, boolean isCollection) {
        this.target = target;
        this.isCollection = isCollection;
    }

    public DeleteStatement(Expression target) {
        this(target, false);
    }

    public Expression getTarget() {
        return target;
    }

    public DeleteExpression toExpression() {
        return new DeleteExpression(target, isCollection);
    }

    public boolean isCollectionTarget() {
        return isCollection;
    }
}
