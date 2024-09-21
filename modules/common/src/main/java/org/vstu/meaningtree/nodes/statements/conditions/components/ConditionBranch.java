package org.vstu.meaningtree.nodes.statements.conditions.components;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Statement;
import org.vstu.meaningtree.nodes.interfaces.HasBodyStatement;
import org.vstu.meaningtree.nodes.statements.CompoundStatement;
import org.vstu.meaningtree.utils.env.SymbolEnvironment;

public class ConditionBranch extends Statement implements HasBodyStatement {
    protected final Expression _condition;
    protected Statement _body;

    public ConditionBranch(Expression condition, Statement body) {
        _condition = condition;
        _body = body;
    }

    public Expression getCondition() {
        return _condition;
    }

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
