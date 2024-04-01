package org.vstu.meaningtree.nodes.unary;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.UnaryExpression;

public class PostfixDecrementOp extends UnaryExpression {
    public PostfixDecrementOp(Expression argument) {
        super(argument);
    }
}
