package org.vstu.meaningtree.nodes.expressions.other;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.expressions.comparison.BinaryComparison;

public class ContainsOp extends BinaryComparison {
    private final boolean isNegative;

    public ContainsOp(Expression left, Expression right, boolean isNegative) {
        super(left, right);
        this.isNegative = isNegative;
    }

    public boolean isNegative() {
        return isNegative;
    }
}
