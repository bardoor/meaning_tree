package org.vstu.meaningtree.nodes.unary;

import org.vstu.meaningtree.nodes.identifiers.Identifier;
import org.vstu.meaningtree.nodes.UnaryExpression;

public class PostfixDecrementOp extends UnaryExpression {
    public PostfixDecrementOp(Identifier argument) {
        super(argument);
    }
}
