package org.vstu.meaningtree.nodes.expressions.unary;

import org.vstu.meaningtree.nodes.expressions.Identifier;
import org.vstu.meaningtree.nodes.expressions.UnaryExpression;

public class PostfixDecrementOp extends UnaryExpression {
    public PostfixDecrementOp(Identifier argument) {
        super(argument);
    }
}
