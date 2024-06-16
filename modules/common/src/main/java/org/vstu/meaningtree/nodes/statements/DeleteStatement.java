package org.vstu.meaningtree.nodes.statements;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Statement;

public class DeleteStatement extends Statement {
    private final Expression _target;

    public DeleteStatement(Expression target) {
        _target = target;
    }

    public Expression getTarget() {
        return _target;
    }
}
