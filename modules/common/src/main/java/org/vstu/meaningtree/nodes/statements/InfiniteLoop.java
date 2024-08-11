package org.vstu.meaningtree.nodes.statements;

import org.vstu.meaningtree.nodes.Statement;

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
