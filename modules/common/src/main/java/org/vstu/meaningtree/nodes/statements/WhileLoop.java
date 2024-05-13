package org.vstu.meaningtree.nodes.statements;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Statement;

public class WhileLoop extends Statement {
    public Expression get_condition() {
        return _condition;
    }

    public Statement get_body() {
        return _body;
    }

    protected final Expression _condition;
    protected final Statement _body;

    public WhileLoop(Expression condition, Statement body) {
        this._condition = condition;
        this._body = body;
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
