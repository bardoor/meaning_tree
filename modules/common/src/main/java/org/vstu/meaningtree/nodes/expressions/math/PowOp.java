package org.vstu.meaningtree.nodes.expressions.math;

import org.vstu.meaningtree.nodes.expressions.BinaryExpression;
import org.vstu.meaningtree.nodes.Expression;

public class PowOp extends BinaryExpression {
    public PowOp(Expression left, Expression right) {
        super(left, right);
    }
}
