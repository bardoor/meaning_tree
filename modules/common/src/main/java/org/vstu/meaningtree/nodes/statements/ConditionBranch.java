package org.vstu.meaningtree.nodes.statements;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Node;
import org.vstu.meaningtree.nodes.Statement;

public class ConditionBranch extends Node {
    public Expression getCondition() {
        return _condition;
    }

    protected final Expression _condition;
    protected final Statement _body;

    public ConditionBranch(Expression condition, Statement body) {
        _condition = condition;
        _body = body;
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
