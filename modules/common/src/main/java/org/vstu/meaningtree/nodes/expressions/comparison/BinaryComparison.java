package org.vstu.meaningtree.nodes.expressions.comparison;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.expressions.BinaryExpression;

public abstract class BinaryComparison extends BinaryExpression {

    public BinaryComparison(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public boolean evaluatesToBoolean() {
        return true;
    }

    public Expression inverse() {
        if (this instanceof GtOp) {
            return new LeOp(_left, _right);
        } else if (this instanceof LtOp) {
            return new GeOp(_left, _right);
        } else if (this instanceof EqOp) {
            return new NotEqOp(_left, _right);
        } else if (this instanceof NotEqOp) {
            return new EqOp(_left, _right);
        } else if (this instanceof GeOp) {
            return new LtOp(_left, _right);
        } else if (this instanceof LeOp) {
            return new GtOp(_left, _right);
        }
        return this;
    }
}
