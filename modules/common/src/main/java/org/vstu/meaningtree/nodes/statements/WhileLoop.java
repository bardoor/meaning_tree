package org.vstu.meaningtree.nodes.statements;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Statement;

public class WhileLoop extends Statement {
    public Expression getCondition() {
        return condition;
    }

    public CompoundStatement getBody() {
        return body;
    }

    protected final Expression condition;
    protected final CompoundStatement body;

    public WhileLoop(Expression condition, CompoundStatement body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
