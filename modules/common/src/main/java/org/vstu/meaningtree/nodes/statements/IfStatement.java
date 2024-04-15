package org.vstu.meaningtree.nodes.statements;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Statement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class IfStatement extends Statement {
    private final List<ConditionBranch> _branches;
    private final Optional<Statement> _elseBranch;


    public IfStatement(Expression condition, Statement thenBranch, Statement elseBranch) {
        super();
        _elseBranch = Optional.ofNullable(elseBranch);
        _branches = new ArrayList<>();
        _branches.add(new ConditionBranch(condition, thenBranch));
    }

    public IfStatement(List<ConditionBranch> branches, Statement elseBranch) {
        super();
        _elseBranch = Optional.ofNullable(elseBranch);
        _branches = new ArrayList<>(branches);
    }

    public Statement getElseBranch() {
        if (!hasElseBranch()) {
            throw new RuntimeException("If statement does not have else branch");
        }

        return _elseBranch.get();
    }

    public boolean hasElseBranch() {
        return _elseBranch.isPresent();
    }

    @Override
    public String generateDot() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%s [label=\"%s\"];\n", _id, getClass().getSimpleName()));

        for (ConditionBranch branch : _branches) {
            builder.append(branch.generateDot());
            builder.append(String.format("%s -- %s;\n", _id, branch.getId()));
        }

        if (_elseBranch.isPresent()) {
            builder.append(_elseBranch.get().generateDot());
            builder.append(String.format("%s -- %s [label=\"%s\"];\n", _id, _elseBranch.get().getId(), "else"));
        }

        return builder.toString();
    }
}
