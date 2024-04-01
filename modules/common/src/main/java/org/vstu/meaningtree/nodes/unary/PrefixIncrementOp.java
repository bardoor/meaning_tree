package org.vstu.meaningtree.nodes.unary;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.UnaryExpression;

public class PrefixIncrementOp extends UnaryExpression {
    public PrefixIncrementOp(Expression argument) {
        super(argument);
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
