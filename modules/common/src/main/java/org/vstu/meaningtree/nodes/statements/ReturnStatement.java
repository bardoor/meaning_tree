package org.vstu.meaningtree.nodes.statements;

import org.jetbrains.annotations.Nullable;
import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Statement;

public class ReturnStatement extends Statement {
    private final Expression _expr;

    public ReturnStatement(Expression expr) {
        _expr = expr;
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
        return _expr;
    }
}
