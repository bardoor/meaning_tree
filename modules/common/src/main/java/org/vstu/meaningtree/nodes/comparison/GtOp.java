package org.vstu.meaningtree.nodes.comparison;


import org.vstu.meaningtree.nodes.Expression;

public class GtOp extends BinaryComparison {
    public GtOp(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
