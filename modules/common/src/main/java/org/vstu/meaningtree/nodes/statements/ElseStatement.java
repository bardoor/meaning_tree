package org.vstu.meaningtree.nodes.statements;

public class ElseStatement extends ConditionStatement {
    protected ElseStatement(CompoundStatement thenBranch) {
        super(thenBranch);
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
