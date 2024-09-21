package org.vstu.meaningtree.nodes.statements.conditions.components;

import org.vstu.meaningtree.nodes.Statement;
import org.vstu.meaningtree.nodes.interfaces.HasBodyStatement;
import org.vstu.meaningtree.nodes.statements.CompoundStatement;
import org.vstu.meaningtree.utils.env.SymbolEnvironment;

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
    public CompoundStatement makeCompoundBody(SymbolEnvironment env) {
        if (!(_body instanceof CompoundStatement)) {
            _body = new CompoundStatement(new SymbolEnvironment(env), getBody());
        }
        return (CompoundStatement) _body;
    }
}
