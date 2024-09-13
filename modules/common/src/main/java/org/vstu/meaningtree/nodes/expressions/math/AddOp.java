package org.vstu.meaningtree.nodes.expressions.math;

import org.vstu.meaningtree.nodes.expressions.BinaryExpression;
import org.vstu.meaningtree.nodes.Expression;

public class AddOp extends BinaryExpression {
    public AddOp(Expression left, Expression right) {
        super(left, right);
    }
}
