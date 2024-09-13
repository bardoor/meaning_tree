package org.vstu.meaningtree.nodes.statements.conditions.components;

import org.vstu.meaningtree.nodes.Statement;
import org.vstu.meaningtree.nodes.interfaces.HasBodyStatement;
import org.vstu.meaningtree.nodes.statements.CompoundStatement;

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
