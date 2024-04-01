package org.vstu.meaningtree.nodes.statements;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Statement;

public class ExpressionStatement extends Statement {

    protected final Expression _expr;

    public ExpressionStatement(Expression expr) {
        _expr = expr;
    }

    public Expression getExpression() {
        return _expr;
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
