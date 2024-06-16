package org.vstu.meaningtree.nodes.statements;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Node;
import org.vstu.meaningtree.nodes.Statement;

public class ConditionBranch extends Node implements HasBodyStatement {
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
    public void makeBodyCompound() {
        if (!(_body instanceof CompoundStatement)) {
            _body = new CompoundStatement(_body);
        }
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
