package org.vstu.meaningtree.nodes.statements;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Statement;
import org.vstu.meaningtree.nodes.declarations.VariableDeclaration;

public class ForEachLoop extends Statement {
    private final VariableDeclaration _item;
    private final Expression _expr;
    private final Statement _body;

    public ForEachLoop(VariableDeclaration item, Expression expr, Statement body) {
        _item = item;
        _expr = expr;
        _body = body;
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }

    public Expression getExpression() {
        return _expr;
    }

    public VariableDeclaration getItem() {
        return _item;
    }

    public Statement getBody() {
        return _body;
    }
}
