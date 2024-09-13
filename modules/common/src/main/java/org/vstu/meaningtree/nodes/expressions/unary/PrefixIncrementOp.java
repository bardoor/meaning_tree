package org.vstu.meaningtree.nodes.expressions.unary;

import org.vstu.meaningtree.nodes.expressions.Identifier;
import org.vstu.meaningtree.nodes.expressions.UnaryExpression;

public class PrefixIncrementOp extends UnaryExpression {
    public PrefixIncrementOp(Identifier argument) {
        super(argument);
    }
}
