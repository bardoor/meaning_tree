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
        return String.format("%s [label=\"%s\"];\n", _id, getClass().getSimpleName())
                + String.format("%s -- %s;\n", _id, _expr.getId())
                + _expr.generateDot();
    }
}
