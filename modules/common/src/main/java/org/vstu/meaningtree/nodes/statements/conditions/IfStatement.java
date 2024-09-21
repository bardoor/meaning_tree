package org.vstu.meaningtree.nodes.statements.conditions;

import org.jetbrains.annotations.Nullable;
import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Statement;
import org.vstu.meaningtree.nodes.statements.conditions.components.ConditionBranch;
import org.vstu.meaningtree.utils.env.SymbolEnvironment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class IfStatement extends Statement {
    private final List<ConditionBranch> _branches;

    @Nullable
    private Statement _elseBranch;

    public IfStatement(Expression condition, @Nullable Statement thenBranch, @Nullable Statement elseBranch) {
        _elseBranch = elseBranch;
        _branches = new ArrayList<>();
        _branches.add(new ConditionBranch(condition, thenBranch));
    }

    public IfStatement(List<ConditionBranch> branches, @Nullable Statement elseBranch) {
        _elseBranch = elseBranch;
        _branches = new ArrayList<>(branches);
    }

    public IfStatement(Expression condition, Statement thenBranch) {
        _branches = new ArrayList<>();
        _branches.add(new ConditionBranch(condition, thenBranch));
        _elseBranch = null;
    }

    public List<ConditionBranch> getBranches() {
        return _branches;
    }

    public Statement getElseBranch() {
        return Objects.requireNonNull(_elseBranch, "If statement does not have else branch");
    }

    public boolean hasElseBranch() {
        return _elseBranch != null;
    }

    @Override
    public String generateDot() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%s [label=\"%s\"];\n", _id, getClass().getSimpleName()));

        for (ConditionBranch branch : _branches) {
            builder.append(branch.generateDot());
            builder.append(String.format("%s -- %s;\n", _id, branch.getId()));
        }

        if (_elseBranch != null) {
            builder.append(_elseBranch.generateDot());
            builder.append(String.format("%s -- %s [label=\"%s\"];\n", _id, _elseBranch.getId(), "else"));
        }

        return builder.toString();
    }

    public void makeCompoundBranches(SymbolEnvironment env) {
        for (ConditionBranch branch : _branches) {
            branch.makeCompoundBody(env);
        }
    }
}
