package org.vstu.meaningtree.nodes.unary;

import org.vstu.meaningtree.nodes.identifiers.Identifier;
import org.vstu.meaningtree.nodes.UnaryExpression;

public class PostfixIncrementOp extends UnaryExpression {
    public PostfixIncrementOp(Identifier argument) {
        super(argument);
    }
}
