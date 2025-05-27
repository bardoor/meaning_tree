package org.vstu.meaningtree.nodes.statements.loops;

import org.vstu.meaningtree.nodes.Statement;
import org.vstu.meaningtree.nodes.statements.CompoundStatement;
import org.vstu.meaningtree.nodes.statements.Loop;
import org.vstu.meaningtree.utils.env.SymbolEnvironment;

public class InfiniteLoop extends Loop {
    private Statement _body;

    public InfiniteLoop(Statement body, LoopType originalType) {
        _body = body;
        _originalType = originalType;
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
