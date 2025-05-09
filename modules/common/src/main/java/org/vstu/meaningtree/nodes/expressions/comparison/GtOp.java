package org.vstu.meaningtree.nodes.expressions.comparison;


import org.vstu.meaningtree.nodes.Expression;

public class GtOp extends BinaryComparison {
    public GtOp(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public Expression tryInvert() {
        return new LeOp(this.getLeft(), this.getRight());
    }
}
