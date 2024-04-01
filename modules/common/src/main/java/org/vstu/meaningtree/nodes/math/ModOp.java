package org.vstu.meaningtree.nodes.math;

import org.vstu.meaningtree.nodes.BinaryExpression;
import org.vstu.meaningtree.nodes.Expression;

public class ModOp extends BinaryExpression {
    public ModOp(Expression left, Expression right) {
        super(left, right);
    }
}
