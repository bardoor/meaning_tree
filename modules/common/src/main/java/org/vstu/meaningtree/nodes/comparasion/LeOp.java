package org.vstu.meaningtree.nodes.comparasion;

import org.vstu.meaningtree.nodes.BinaryExpression;
import org.vstu.meaningtree.nodes.Expression;

public class LeOp extends BinaryExpression {
    public LeOp(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}