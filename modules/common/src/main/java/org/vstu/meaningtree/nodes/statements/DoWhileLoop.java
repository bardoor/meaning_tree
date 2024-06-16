package org.vstu.meaningtree.nodes.statements;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Statement;

public class DoWhileLoop extends Statement {
    protected final Expression condition;
    protected final CompoundStatement body;

    public DoWhileLoop(Expression condition, CompoundStatement body) {
        this.condition = condition;
        this.body = body;
    }

    public Expression getCondition() {
        return condition;
    }

    public CompoundStatement getBody() {
        return body;
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
