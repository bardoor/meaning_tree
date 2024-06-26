package org.vstu.meaningtree.nodes.statements;

import org.vstu.meaningtree.nodes.Statement;
import java.util.Arrays;

public interface HasBodyStatement {
    Statement getBody();

    default CompoundStatement getBodyAsCompoundStatement() {
        if (getBody() instanceof CompoundStatement compoundStatement) {
            return new CompoundStatement(Arrays.asList(compoundStatement.getNodes()));
        } else {
            makeBodyCompound();
            return (CompoundStatement) getBody();
        }
    }

    void makeBodyCompound();
}
