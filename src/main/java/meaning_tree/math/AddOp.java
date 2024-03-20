package meaning_tree.math;

import meaning_tree.BinaryExpression;
import meaning_tree.Expression;

public class AddOp extends BinaryExpression {
    public AddOp(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
