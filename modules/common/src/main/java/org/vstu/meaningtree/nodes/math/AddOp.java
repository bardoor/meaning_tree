package org.vstu.meaningtree.nodes.math;

import org.vstu.meaningtree.nodes.BinaryExpression;
import org.vstu.meaningtree.nodes.Expression;

public class AddOp extends BinaryExpression {
    public AddOp(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
