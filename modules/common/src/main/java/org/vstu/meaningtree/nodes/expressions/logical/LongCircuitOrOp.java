package org.vstu.meaningtree.nodes.expressions.logical;

import org.vstu.meaningtree.nodes.expressions.BinaryExpression;
import org.vstu.meaningtree.nodes.Expression;

public class LongCircuitOrOp extends BinaryExpression {
    public LongCircuitOrOp(Expression left, Expression right) {
        super(left, right);
    }
}
