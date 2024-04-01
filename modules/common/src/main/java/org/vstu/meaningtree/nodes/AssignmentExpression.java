package org.vstu.meaningtree.nodes;

public class AssignmentExpression extends Expression {
    private final Identifier lvalue;
    private final Expression rvalue;

    public AssignmentExpression(Identifier id, Expression value) {
        this.lvalue = id;
        this.rvalue = value;
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
