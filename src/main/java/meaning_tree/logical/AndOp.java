package meaning_tree.logical;

import meaning_tree.BinaryExpression;
import meaning_tree.Expression;

public class AndOp extends BinaryExpression {
    public AndOp(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
