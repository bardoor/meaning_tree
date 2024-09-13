package org.vstu.meaningtree.nodes.statements.conditions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Statement;
import org.vstu.meaningtree.nodes.statements.conditions.components.DefaultCaseBlock;
import org.vstu.meaningtree.nodes.statements.conditions.components.FallthroughCaseBlock;
import org.vstu.meaningtree.nodes.statements.conditions.components.CaseBlock;

import java.util.*;
import java.util.stream.Stream;

public class SwitchStatement extends Statement {
    private final Expression _targetExpression;
    private final List<CaseBlock> _cases;
    private final DefaultCaseBlock _defaultCase;

    public SwitchStatement(@NotNull Expression targetExpression,
                           @NotNull List<CaseBlock> cases) {
        _targetExpression = targetExpression;

        _defaultCase = findDefaultCase(cases);

        _cases = new ArrayList<>(cases);
        _cases.remove(_defaultCase);
    }

    public SwitchStatement(@NotNull Expression targetExpression,
                           @NotNull List<CaseBlock> cases,
                           @Nullable DefaultCaseBlock defaultCaseBlock) {
        this(targetExpression, Stream.concat(cases.stream(), Stream.of(defaultCaseBlock)).toList());
    }

    @Nullable
    private DefaultCaseBlock findDefaultCase(@NotNull List<CaseBlock> cases) {
        DefaultCaseBlock defaultCaseBlock = null;

        boolean found = false;
        for (CaseBlock caseBlock : cases) {
            if (found && caseBlock instanceof DefaultCaseBlock) {
                throw new IllegalArgumentException(this.getClass() + " cannot have several default branches");
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

    @NotNull
    public Expression getTargetExpression() {
        return _targetExpression;
    }

    @NotNull
    public List<CaseBlock> getCases() {
        return _cases;
    }

    @Nullable
    public DefaultCaseBlock getDefaultCase() {
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
