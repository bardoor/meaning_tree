package org.vstu.meaningtree.nodes.expressions.bitwise;

import org.vstu.meaningtree.nodes.expressions.BinaryExpression;
import org.vstu.meaningtree.nodes.Expression;

public class LeftShiftOp extends BinaryExpression {
    public LeftShiftOp(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
