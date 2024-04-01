package org.vstu.meaningtree.nodes.unary;

import org.vstu.meaningtree.nodes.BinaryExpression;
import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.UnaryExpression;

public class PrefixDecrementOp extends UnaryExpression {
    public PrefixDecrementOp(Expression value) {
        super(value);
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
