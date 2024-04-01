package org.vstu.meaningtree.nodes.statements;

import org.vstu.meaningtree.nodes.Statement;

public abstract class ConditionStatement extends Statement {
    protected final CompoundStatement _body;

    public CompoundStatement getBody() {
        return _body;
    }

    protected ConditionStatement(CompoundStatement thenBranch) {
        _body = thenBranch;
    }
}
