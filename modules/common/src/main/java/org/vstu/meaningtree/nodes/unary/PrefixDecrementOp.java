package org.vstu.meaningtree.nodes.unary;

import org.vstu.meaningtree.nodes.identifiers.Identifier;
import org.vstu.meaningtree.nodes.UnaryExpression;

public class PrefixDecrementOp extends UnaryExpression {
    public PrefixDecrementOp(Identifier argument) {
        super(argument);
    }
}
