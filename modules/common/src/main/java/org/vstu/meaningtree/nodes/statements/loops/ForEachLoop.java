package org.vstu.meaningtree.nodes.statements.loops;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Statement;
import org.vstu.meaningtree.nodes.declarations.VariableDeclaration;
import org.vstu.meaningtree.nodes.statements.CompoundStatement;
import org.vstu.meaningtree.utils.env.SymbolEnvironment;

public class ForEachLoop extends ForLoop {
    private final VariableDeclaration _item;
    private final Expression _expr;
    private Statement _body;

    public ForEachLoop(VariableDeclaration item, Expression expr, Statement body) {
        _item = item;
        _expr = expr;
        _body = body;
    }

    @Override
    public CompoundStatement makeCompoundBody(SymbolEnvironment env) {
        if (!(_body instanceof CompoundStatement)) {
            _body = new CompoundStatement(new SymbolEnvironment(env), getBody());
        }
        return (CompoundStatement) _body;
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

