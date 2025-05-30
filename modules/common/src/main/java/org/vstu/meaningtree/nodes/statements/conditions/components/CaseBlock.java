package org.vstu.meaningtree.nodes.statements.conditions.components;

import org.vstu.meaningtree.iterators.utils.TreeNode;
import org.vstu.meaningtree.nodes.Statement;
import org.vstu.meaningtree.nodes.interfaces.HasBodyStatement;
import org.vstu.meaningtree.nodes.statements.CompoundStatement;
import org.vstu.meaningtree.utils.env.SymbolEnvironment;

public abstract class CaseBlock extends Statement implements HasBodyStatement {
    @TreeNode private Statement body;

    public CaseBlock(Statement body) {
        this.body = body;
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
