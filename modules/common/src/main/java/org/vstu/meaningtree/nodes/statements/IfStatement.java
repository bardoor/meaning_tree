package org.vstu.meaningtree.nodes.statements;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Statement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class IfStatement extends Statement {
    private final List<ConditionBranch> _branches = new ArrayList<>();
    private final Optional<Statement> _elseBranch;


    public IfStatement(Expression condition, Statement thenBranch, Statement elseBranch) {
        super();
        _elseBranch = Optional.ofNullable(elseBranch);
        _branches.add(new ConditionBranch(condition, thenBranch));
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
        //TODO: fix for new structure
        throw new UnsupportedOperationException();
        /*
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%s [label=\"%s\"];\n", _id, getClass().getSimpleName()));

        // builder.append(_body.generateDot());
        // builder.append(_condition.generateDot());
        // builder.append(String.format("%s -- %s [label=\"%s\"];\n", _id, _body.getId(), "body"));
        // builder.append(String.format("%s -- %s [label=\"%s\"];\n", _id, _condition.getId(), "condition"));

        if (_elseBranch.isPresent()) {
            builder.append(_elseBranch.get().generateDot());
            builder.append(String.format("%s -- %s [label=\"%s\"];\n", _id, _elseBranch.get().getId(), "else"));
        }

        return builder.toString();
        */
    }
}
