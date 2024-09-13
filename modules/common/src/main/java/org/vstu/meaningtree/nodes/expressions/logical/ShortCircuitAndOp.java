package org.vstu.meaningtree.nodes.expressions.logical;

import org.vstu.meaningtree.nodes.expressions.BinaryExpression;
import org.vstu.meaningtree.nodes.Expression;

public class ShortCircuitAndOp extends BinaryExpression {
    public ShortCircuitAndOp(Expression left, Expression right) {
        super(left, right);
    }
}
