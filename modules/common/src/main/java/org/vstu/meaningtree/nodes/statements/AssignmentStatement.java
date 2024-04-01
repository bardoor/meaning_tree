package org.vstu.meaningtree.nodes.statements;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Identifier;
import org.vstu.meaningtree.nodes.Statement;

public class AssignmentStatement extends Statement {
    private final Identifier lvalue;
    private final Expression rvalue;

    public AssignmentStatement(Identifier id, Expression value) {
        this.lvalue = id;
        this.rvalue = value;
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
