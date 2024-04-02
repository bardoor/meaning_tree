package org.vstu.meaningtree.nodes.statements;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Identifier;
import org.vstu.meaningtree.nodes.CanInitialize;
import org.vstu.meaningtree.nodes.Statement;

public class AssignmentStatement extends Statement implements CanInitialize {
    private final Identifier _lvalue;
    private final Expression _rvalue;

    public AssignmentStatement(Identifier id, Expression value) {
        _lvalue = id;
        _rvalue = value;
    }

    public Identifier getLValue() {
        return _lvalue;
    }

    public Expression getRValue() {
        return _rvalue;
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
