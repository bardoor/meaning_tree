package org.vstu.meaningtree.nodes.comparison;


import org.vstu.meaningtree.nodes.Expression;

public class GeOp extends BinaryComparison {
    public GeOp(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
