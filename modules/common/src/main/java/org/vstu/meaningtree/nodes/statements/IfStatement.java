package org.vstu.meaningtree.nodes.statements;

import org.vstu.meaningtree.nodes.Expression;

import java.util.Optional;

public class IfStatement extends ConditionStatement {
    private final Expression _condition;
    private final Optional<ConditionStatement> _elseBranch;

    public IfStatement(Expression condition, CompoundStatement thenBranch) {
        this(condition, thenBranch, null);
    }

    public IfStatement(Expression condition, CompoundStatement thenBranch, ConditionStatement elseBranch) {
        super(thenBranch);
        _condition = condition;
        _elseBranch = Optional.ofNullable(elseBranch);
    }

    public Expression getCondition() {
        return _condition;
    }

    public Optional<ConditionStatement> getElseBranch() {
        return _elseBranch;
    }

    @Override
    public String generateDot() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%s [label=\"%s\"]\n", _id, getClass().getSimpleName()));
        builder.append(String.format("%s -> %s\n", _id, _condition.getId()));
        builder.append(_condition.generateDot());

        if (_elseBranch.isPresent()) {
            builder.append(String.format("%s -> %s\n", _id, _elseBranch.get().getId()));
            builder.append(_elseBranch.get().generateDot());
        }

        return builder.toString();
    }
}
