package org.vstu.meaningtree.nodes.unary;

import org.vstu.meaningtree.nodes.identifiers.Identifier;
import org.vstu.meaningtree.nodes.UnaryExpression;

public class PrefixIncrementOp extends UnaryExpression {
    public PrefixIncrementOp(Identifier argument) {
        super(argument);
    }
}
