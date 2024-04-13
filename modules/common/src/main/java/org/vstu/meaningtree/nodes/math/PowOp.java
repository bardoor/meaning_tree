package org.vstu.meaningtree.nodes.math;

import org.vstu.meaningtree.nodes.BinaryExpression;
import org.vstu.meaningtree.nodes.Expression;

public class PowOp extends BinaryExpression {
    public PowOp(Expression left, Expression right) {
        super(left, right);
    }
}
