package org.vstu.meaningtree.nodes.expressions.unary;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.expressions.UnaryExpression;

public class PrefixIncrementOp extends UnaryExpression {
    public PrefixIncrementOp(Expression argument) {
        super(argument);
    }
}
