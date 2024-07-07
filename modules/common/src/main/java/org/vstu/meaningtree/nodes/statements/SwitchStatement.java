package org.vstu.meaningtree.nodes.statements;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Statement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SwitchStatement extends Statement {
    private final Expression _targetExpression;
    private final List<ConditionBranch> _cases;
    private Statement _defaultCase;

    public SwitchStatement(Expression targetExpression, List<ConditionBranch> cases, Statement defaultCase) {
        _targetExpression = targetExpression;
        _cases = new ArrayList<>(cases);
        _defaultCase = defaultCase;
    }

    public SwitchStatement(Expression targetExpression, List<ConditionBranch> cases) {
        this(targetExpression, cases, null);
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }

    public Expression getTargetExpression() {
        return _targetExpression;
    }

    public List<ConditionBranch> getCases() {
        return new ArrayList<>(_cases);
    }

    public Statement getDefaultCase() {
        if (!hasDefaultCase()) {
            throw new RuntimeException("Switch has not default case");
        }
        return _defaultCase;
    }

    public boolean hasDefaultCase() {
        return _defaultCase != null;
    }

    public void makeBodyCompound() {
        if (hasDefaultCase()) {
            if (!(getDefaultCase() instanceof CompoundStatement)) {
                _defaultCase = new CompoundStatement(getDefaultCase());
            }
        }
        for (ConditionBranch branch : _cases) {
            branch.makeBodyCompound();
        }
    }
}
