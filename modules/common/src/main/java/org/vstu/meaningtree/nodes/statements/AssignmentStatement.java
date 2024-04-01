package org.vstu.meaningtree.nodes.statements;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Identifier;
import org.vstu.meaningtree.nodes.Statement;

public class AssignmentStatement extends Statement {
    private final Identifier _lvalue;
    private final Expression _rvalue;

    public AssignmentStatement(Identifier id, Expression value) {
        _lvalue = id;
        _rvalue = value;
    }

    public Identifier getLeft() {
        return _lvalue;
    }

    public Expression getRight() {
        return _rvalue;
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
