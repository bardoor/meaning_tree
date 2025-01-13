package org.vstu.meaningtree.nodes.expressions.unary;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.expressions.UnaryExpression;

public class PostfixDecrementOp extends UnaryExpression {
    public PostfixDecrementOp(Expression argument) {
        super(argument);
    }
}
