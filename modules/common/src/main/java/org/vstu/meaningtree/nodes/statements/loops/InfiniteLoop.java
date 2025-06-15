package org.vstu.meaningtree.nodes.statements.loops;

import org.vstu.meaningtree.iterators.utils.TreeNode;
import org.vstu.meaningtree.nodes.Statement;
import org.vstu.meaningtree.nodes.statements.CompoundStatement;
import org.vstu.meaningtree.nodes.statements.Loop;
import org.vstu.meaningtree.utils.env.SymbolEnvironment;

public class InfiniteLoop extends Loop {
    @TreeNode private Statement body;

    public InfiniteLoop(Statement body, LoopType originalType) {
        this.body = body;
        _originalType = originalType;
    }

    @Override
    public Statement getBody() {
        return body;
    }

    @Override
    public CompoundStatement makeCompoundBody(SymbolEnvironment env) {
        if (!(body instanceof CompoundStatement)) {
            body = new CompoundStatement(new SymbolEnvironment(env), getBody());
        }
        return (CompoundStatement) body;
    }
}
