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

    @Override
    public String generateDot() {
        return String.format("%s [label=\"%s\"];\n", _id, _id.getClass().getSimpleName())
                + _body.generateDot()
                + String.format("%s -- %s;\n", _id, _body.getId());
    }
}
