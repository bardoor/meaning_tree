package org.vstu.meaningtree.nodes.unary;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.UnaryExpression;

public class PostfixIncrementOp extends UnaryExpression {
    public PostfixIncrementOp(Expression argument) {
        super(argument);
    }
}
