package org.vstu.meaningtree.nodes.expressions.unary;

import org.vstu.meaningtree.nodes.expressions.Identifier;
import org.vstu.meaningtree.nodes.expressions.UnaryExpression;

public class PostfixIncrementOp extends UnaryExpression {
    public PostfixIncrementOp(Identifier argument) {
        super(argument);
    }
}
