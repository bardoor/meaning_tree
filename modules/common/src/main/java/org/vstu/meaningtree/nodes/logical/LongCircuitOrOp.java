package org.vstu.meaningtree.nodes.logical;

import org.vstu.meaningtree.nodes.BinaryExpression;
import org.vstu.meaningtree.nodes.Expression;

public class LongCircuitOrOp extends BinaryExpression {
    public LongCircuitOrOp(Expression left, Expression right) {
        super(left, right);
    }
}
