package org.vstu.meaningtree.nodes.statements.loops;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Statement;
import org.vstu.meaningtree.nodes.statements.CompoundStatement;
import org.vstu.meaningtree.nodes.statements.Loop;
import org.vstu.meaningtree.utils.env.SymbolEnvironment;

public class WhileLoop extends Loop {
    public Expression getCondition() {
        return _condition;
    }

    public Statement getBody() {
        return _body;
    }

    protected final Expression _condition;
    protected Statement _body;

    public WhileLoop(Expression condition, Statement body) {
        this._condition = condition;
        this._body = body;
    }

    @Override
    public CompoundStatement makeCompoundBody(SymbolEnvironment env) {
        if (!(_body instanceof CompoundStatement)) {
            _body = new CompoundStatement(new SymbolEnvironment(env), getBody());
        }
        return (CompoundStatement) _body;
    }

    @Override
    public String generateDot() {
        StringBuilder builder = new StringBuilder();

        builder.append(String.format("%s [label=\"%s\"];\n", _id, getClass().getSimpleName()));

        builder.append(_condition.generateDot());
        builder.append(_body.generateDot());

        builder.append(String.format("%s -- %s [label=\"%s\"];\n", _id, _condition.getId(), "condition"));
        builder.append(String.format("%s -- %s [label=\"%s\"];\n", _id, _body.getId(), "body"));

        return builder.toString();
    }
}
