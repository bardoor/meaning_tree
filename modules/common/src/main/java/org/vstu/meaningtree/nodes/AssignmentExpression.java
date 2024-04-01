package org.vstu.meaningtree.nodes;

public class AssignmentExpression extends Expression {
    private final Identifier _lvalue;
    private final Expression _rvalue;

    public AssignmentExpression(Identifier id, Expression value) {
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
