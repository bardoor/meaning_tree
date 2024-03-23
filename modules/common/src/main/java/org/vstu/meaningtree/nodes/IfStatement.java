package org.vstu.meaningtree.nodes;

import java.util.Optional;

public class IfStatement extends Statement {
    private final Expression _condition;
    private final CompoundStatement _thenBranch;
    private final Optional<CompoundStatement> _elseBranch;

    public IfStatement(Expression condition, CompoundStatement thenBranch) {
        this(condition, thenBranch, null);
    }

    public IfStatement(Expression condition, CompoundStatement thenBranch, CompoundStatement elseBranch) {
        _condition = condition;
        _thenBranch = thenBranch;
        _elseBranch = Optional.ofNullable(elseBranch);
    }

    public Expression getCondition() {
        return _condition;
    }

    public CompoundStatement getThenBranch() {
        return _thenBranch;
    }

    public Optional<CompoundStatement> getElseBranch() {
        return _elseBranch;
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
