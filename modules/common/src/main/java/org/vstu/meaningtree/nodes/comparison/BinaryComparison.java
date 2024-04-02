package org.vstu.meaningtree.nodes.comparison;

import org.vstu.meaningtree.nodes.BinaryExpression;
import org.vstu.meaningtree.nodes.Expression;

public abstract class BinaryComparison extends BinaryExpression {

    public BinaryComparison(Expression left, Expression right) {
        super(left, right);
    }
}
