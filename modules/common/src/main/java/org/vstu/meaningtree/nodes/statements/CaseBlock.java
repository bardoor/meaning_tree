package org.vstu.meaningtree.nodes.statements;

import org.vstu.meaningtree.nodes.Statement;

public abstract class CaseBlock extends Statement implements HasBodyStatement {
    private Statement _body;

    public CaseBlock(Statement body) {
        _body = body;
    }

    @Override
    public Statement getBody() {
        return _body;
    }

    @Override
    public void makeBodyCompound() {
        if (_body instanceof CompoundStatement) {
            return;
        }

        _body = new CompoundStatement(_body);
    }
}
