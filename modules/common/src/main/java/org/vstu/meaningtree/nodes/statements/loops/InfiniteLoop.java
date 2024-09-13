package org.vstu.meaningtree.nodes.statements.loops;

import org.vstu.meaningtree.nodes.Statement;
import org.vstu.meaningtree.nodes.statements.CompoundStatement;
import org.vstu.meaningtree.nodes.statements.Loop;

public class InfiniteLoop extends Loop {
    private Statement _body;

    public InfiniteLoop(Statement body) {
        _body = body;
    }

    @Override
    public Statement getBody() {
        return _body;
    }

    @Override
    public void makeBodyCompound() {
        if (!(_body instanceof CompoundStatement)) {
            _body = new CompoundStatement(_body);
        }
    }
}
