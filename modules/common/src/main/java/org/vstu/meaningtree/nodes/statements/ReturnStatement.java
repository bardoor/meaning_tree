package org.vstu.meaningtree.nodes.statements;

import org.jetbrains.annotations.Nullable;
import org.vstu.meaningtree.iterators.utils.TreeNode;
import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Statement;

public class ReturnStatement extends Statement {
    @TreeNode private Expression expression;

    public ReturnStatement(Expression expr) {
        expression = expr;
    }

    public ReturnStatement() {
        this(null);
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }

    @Nullable
    public Expression getExpression() {
        return expression;
    }
}
