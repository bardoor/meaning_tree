package org.vstu.meaningtree.nodes.statements;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Statement;

public class ConditionBranch {
    public Expression getCondition() {
        return _condition;
    }

    protected final Expression _condition;
    protected final Statement _body;

    public ConditionBranch(Expression condition, Statement body) {
        _condition = condition;
        _body = body;
    }

    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
