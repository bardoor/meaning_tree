package org.vstu.meaningtree.nodes.unary;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.UnaryExpression;

public class UnaryMinusOp extends UnaryExpression {
    public UnaryMinusOp(Expression argument) {
        super(argument);
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
