package org.vstu.meaningtree.nodes.expressions.unary;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.expressions.UnaryExpression;

public class PostfixIncrementOp extends UnaryExpression {
    public PostfixIncrementOp(Expression argument) {
        super(argument);
    }
}
