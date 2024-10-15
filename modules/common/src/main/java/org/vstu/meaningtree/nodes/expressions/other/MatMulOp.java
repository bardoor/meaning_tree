package org.vstu.meaningtree.nodes.expressions.other;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.expressions.BinaryExpression;

public class MatMulOp extends BinaryExpression {

    public MatMulOp(Expression left, Expression right) {
        super(left, right);
    }
}
