package org.vstu.meaningtree.nodes.statements;

import org.jetbrains.annotations.Nullable;
import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Statement;

import java.util.*;
import java.util.stream.Stream;

public class SwitchStatement extends Statement {
    private final Expression _targetExpression;
    private final List<CaseBlock> _cases;
    private final DefaultCaseBlock _defaultCase;

    public SwitchStatement(Expression targetExpression,
                            List<CaseBlock> cases) {
        _targetExpression = targetExpression;
        _cases = cases;
        _defaultCase = findDefaultCase(_cases);
    }

    public SwitchStatement(Expression targetExpression,
                           List<CaseBlock> cases,
                           DefaultCaseBlock defaultCaseBlock) {
        this(targetExpression, Stream.concat(cases.stream(), Stream.of(defaultCaseBlock)).toList());
    }

    @Nullable
    public DefaultCaseBlock findDefaultCase(List<CaseBlock> cases) {
        DefaultCaseBlock defaultCaseBlock = null;

        boolean found = false;
        for (CaseBlock caseBlock : cases) {
            if (found && caseBlock instanceof DefaultCaseBlock) {
                throw new IllegalArgumentException("Switch cannot have several default branches");
            }

            if (caseBlock instanceof DefaultCaseBlock dcb) {
                defaultCaseBlock = dcb;
                found = true;
            }
        }

        return defaultCaseBlock;
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }

    public Expression getTargetExpression() {
        return _targetExpression;
    }

    public List<CaseBlock> getCases() {
        return _cases;
    }

    public DefaultCaseBlock getDefaultCase() {
        if (!hasDefaultCase()) {
            throw new RuntimeException("Switch has not default case");
        }
        return _defaultCase;
    }

    public boolean hasDefaultCase() {
        return _defaultCase != null;
    }

    public List<FallthroughCaseBlock> getFallthroughCaseBlocks() {
        return _cases
                .stream()
                .filter(caseBlock -> caseBlock instanceof FallthroughCaseBlock)
                .map(caseBlock -> (FallthroughCaseBlock) caseBlock)
                .toList();
    }

    public void makeBodyCompound() {
        for (CaseBlock caseBlock : _cases) {
            caseBlock.makeBodyCompound();
        }
    }
}
