package org.vstu.meaningtree.nodes.statements;

public class ElseStatement extends ConditionStatement {
    public ElseStatement(CompoundStatement thenBranch) {
        super(thenBranch);
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
