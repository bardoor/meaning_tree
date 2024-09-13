package org.vstu.meaningtree.nodes.expressions.math;

import org.vstu.meaningtree.nodes.expressions.BinaryExpression;
import org.vstu.meaningtree.nodes.Expression;

public class ModOp extends BinaryExpression {
    public ModOp(Expression left, Expression right) {
        super(left, right);
    }
}
