package org.vstu.meaningtree.nodes.statements;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Statement;

public class WhileLoop extends Statement {
    public Expression getCondition() {
        return condition;
    }

    public Statement getBody() {
        return body;
    }

    protected final Expression condition;
    protected final Statement body;

    public WhileLoop(Expression condition, Statement body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
