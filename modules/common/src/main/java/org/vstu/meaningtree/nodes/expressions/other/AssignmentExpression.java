package org.vstu.meaningtree.nodes.expressions.other;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.enums.AugmentedAssignmentOperator;
import org.vstu.meaningtree.nodes.expressions.BinaryExpression;
import org.vstu.meaningtree.nodes.interfaces.HasInitialization;
import org.vstu.meaningtree.nodes.statements.assignments.AssignmentStatement;

import java.util.Objects;

public class AssignmentExpression extends BinaryExpression implements HasInitialization {
    private final AugmentedAssignmentOperator _op;

    public AssignmentExpression(Expression id, Expression value, AugmentedAssignmentOperator op) {
        super(id, value);
        _op = op;
    }

    public AssignmentStatement toStatement() {
        return new AssignmentStatement(_left, _right, _op);
    }

    public AssignmentExpression(Expression id, Expression value) {
        this(id, value, AugmentedAssignmentOperator.NONE);
    }

    public AugmentedAssignmentOperator getAugmentedOperator() {
        return _op;
    }

    public Expression getLValue() {
        return _left;
    }

    public Expression getRValue() {
        return _right;
    }

    @Override
    public String generateDot() {
        StringBuilder builder = new StringBuilder();

        builder.append(String.format("%s [label=\"%s\"];\n", _id, _op.toString()));

        builder.append(_left.generateDot());
        builder.append(_right.generateDot());

        builder.append(String.format("%s -- %s;\n", _id, _left.getId()));
        builder.append(String.format("%s -- %s;\n", _id, _right.getId()));

        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssignmentExpression that = (AssignmentExpression) o;
        return Objects.equals(_left, that._left) && Objects.equals(_right, that._right) && _op == that._op;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), _left, _right, _op);
    }

    @Override
    public AssignmentExpression clone() {
        AssignmentExpression obj = (AssignmentExpression) super.clone();
        obj._left = _left.clone();
        obj._right = _right.clone();
        return obj;
    }
}
