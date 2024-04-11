package org.vstu.meaningtree.nodes.statements;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Statement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SwitchStatement extends Statement {
    private final Expression targetExpression;
    private final List<ConditionBranch> cases;
    private final Optional<Statement> defaultCase;

    public SwitchStatement(Expression targetExpression, List<ConditionBranch> cases, Statement defaultCase) {
        this.targetExpression = targetExpression;
        this.cases = new ArrayList<>(cases);
        this.defaultCase = Optional.ofNullable(defaultCase);
    }

    public SwitchStatement(Expression targetExpression, List<ConditionBranch> cases) {
        this(targetExpression, cases, null);
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }

    public Expression getTargetExpression() {
        return targetExpression;
    }

    public List<ConditionBranch> getCases() {
        return new ArrayList<>(cases);
    }

    public Statement getDefaultCase() {
        if (!hasDefaultCase()) {
            throw new RuntimeException("Switch has not default case");
        }
        return defaultCase.get();
    }

    public boolean hasDefaultCase() {
        return defaultCase.isPresent();
    }
}
