package org.vstu.meaningtree.nodes.statements;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Statement;

public class DoWhileLoop extends Loop {
    protected final Expression condition;
    protected Statement body;

    public DoWhileLoop(Expression condition, Statement body) {
        this.condition = condition;
        this.body = body;
    }

    public Expression getCondition() {
        return condition;
    }

    public Statement getBody() {
        return body;
    }

    @Override
    public void makeBodyCompound() {
        if (!(body instanceof CompoundStatement)) {
            body = new CompoundStatement(body);
        }
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
