package org.vstu.meaningtree.nodes.expressions.unary;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.expressions.UnaryExpression;

public class PrefixDecrementOp extends UnaryExpression {
    public PrefixDecrementOp(Expression argument) {
        super(argument);
    }
}
