package org.vstu.meaningtree.nodes.statements;

import org.vstu.meaningtree.nodes.Expression;

import java.util.Optional;

public class IfStatement extends ConditionStatement {
    private final Expression _condition;
    private final Optional<CompoundStatement> _elseBranch;

    public IfStatement(Expression condition, CompoundStatement thenBranch) {
        this(condition, thenBranch, null);
    }

    public IfStatement(Expression condition, CompoundStatement thenBranch, CompoundStatement elseBranch) {
        super(thenBranch);
        _condition = condition;
        _elseBranch = Optional.ofNullable(elseBranch);
    }

    public Expression getCondition() {
        return _condition;
    }

    public Optional<CompoundStatement> getElseBranch() {
        return _elseBranch;
    }

    @Override
    public String generateDot() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%s [label=\"%s\"];\n", _id, getClass().getSimpleName()));
        builder.append(_body.generateDot());
        builder.append(_condition.generateDot());
        builder.append(String.format("%s -- %s [label=\"%s\"];\n", _id, _body.getId(), "body"));
        builder.append(String.format("%s -- %s [label=\"%s\"];\n", _id, _condition.getId(), "condition"));

        if (_elseBranch.isPresent()) {
            builder.append(_elseBranch.get().generateDot());
            builder.append(String.format("%s -- %s [label=\"%s\"];\n", _id, _elseBranch.get().getId(), "else"));
        }

        return builder.toString();
    }
}
