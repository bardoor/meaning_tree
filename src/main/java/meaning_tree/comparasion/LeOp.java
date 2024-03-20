package meaning_tree.comparasion;

import meaning_tree.BinaryExpression;
import meaning_tree.Expression;

public class LeOp extends BinaryExpression {
    public LeOp(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
