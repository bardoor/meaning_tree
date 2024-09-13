package org.vstu.meaningtree.nodes.expressions.logical;

import org.vstu.meaningtree.nodes.expressions.BinaryExpression;
import org.vstu.meaningtree.nodes.Expression;

public class LongCircuitAndOp extends BinaryExpression {
    public LongCircuitAndOp(Expression left, Expression right) {
        super(left, right);
    }
}
