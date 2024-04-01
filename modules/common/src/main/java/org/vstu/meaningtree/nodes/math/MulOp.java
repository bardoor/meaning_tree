package org.vstu.meaningtree.nodes.math;

import org.vstu.meaningtree.nodes.BinaryExpression;
import org.vstu.meaningtree.nodes.Expression;

public class MulOp extends BinaryExpression {
    public MulOp(Expression left, Expression right) {
        super(left, right);
    }
}
