package org.vstu.meaningtree.nodes.expressions.comparison;


import org.vstu.meaningtree.nodes.Expression;

public class EqOp extends BinaryComparison {
    public EqOp(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
