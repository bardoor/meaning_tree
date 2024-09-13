package org.vstu.meaningtree.nodes.expressions.unary;

import org.vstu.meaningtree.nodes.expressions.Identifier;
import org.vstu.meaningtree.nodes.expressions.UnaryExpression;

public class PrefixDecrementOp extends UnaryExpression {
    public PrefixDecrementOp(Identifier argument) {
        super(argument);
    }
}
