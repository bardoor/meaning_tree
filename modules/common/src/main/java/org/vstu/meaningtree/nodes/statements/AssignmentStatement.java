package org.vstu.meaningtree.nodes.statements;

import org.vstu.meaningtree.nodes.*;

public class AssignmentStatement extends Statement implements HasInitialization {
    private final Expression _lvalue;
    private final Expression _rvalue;
    private final AugmentedAssignmentOperator _op;

    public AssignmentStatement(Expression id, Expression value, AugmentedAssignmentOperator op) {
        _lvalue = id;
        _rvalue = value;
        _op = op;
    }

    public AssignmentStatement(Expression id, Expression value) {
        this(id, value, AugmentedAssignmentOperator.NONE);
    }

    public AugmentedAssignmentOperator getAugmentedOperator() {
        return _op;
    }

    public Expression getLValue() {
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
