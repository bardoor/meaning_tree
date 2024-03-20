package meaning_tree.math;

import meaning_tree.BinaryExpression;
import meaning_tree.Expression;

public class SubOp extends BinaryExpression {
    public SubOp(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
