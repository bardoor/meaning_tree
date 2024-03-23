package org.vstu.meaningtree.nodes.math;

import org.vstu.meaningtree.nodes.BinaryExpression;
import org.vstu.meaningtree.nodes.Expression;

public class DivOp extends BinaryExpression {
    public DivOp(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
