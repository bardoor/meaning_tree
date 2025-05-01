package org.vstu.meaningtree.nodes.statements.loops;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Statement;
import org.vstu.meaningtree.nodes.statements.CompoundStatement;
import org.vstu.meaningtree.nodes.statements.Loop;
import org.vstu.meaningtree.utils.TreeNode;
import org.vstu.meaningtree.utils.env.SymbolEnvironment;

public class DoWhileLoop extends Loop {
    @TreeNode protected Expression condition;
    @TreeNode protected Statement body;

    public DoWhileLoop(Expression condition, Statement body) {
        this.condition = condition;
        this.body = body;
    }

    public Expression getCondition() {
        return condition;
    }

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

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
