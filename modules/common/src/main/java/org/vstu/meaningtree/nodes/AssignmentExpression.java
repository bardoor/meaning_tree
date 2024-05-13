package org.vstu.meaningtree.nodes;

public class AssignmentExpression extends Expression implements HasInitialization {
    private final Expression _lvalue;
    private final Expression _rvalue;
    private final AugmentedAssignmentOperator _op;

    public AssignmentExpression(Expression id, Expression value, AugmentedAssignmentOperator op) {
        _lvalue = id;
        _rvalue = value;
        _op = op;
    }

    public AssignmentExpression(Expression id, Expression value) {
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
        StringBuilder builder = new StringBuilder();

        builder.append(String.format("%s [label=\"%s\"];\n", _id, _op.toString()));

        builder.append(_lvalue.generateDot());
        builder.append(_rvalue.generateDot());

        builder.append(String.format("%s -- %s;\n", _id, _lvalue.getId()));
        builder.append(String.format("%s -- %s;\n", _id, _rvalue.getId()));

        return builder.toString();
    }
}
