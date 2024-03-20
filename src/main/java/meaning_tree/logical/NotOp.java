package meaning_tree.logical;

import meaning_tree.Expression;
import meaning_tree.UnaryExpression;

public class NotOp extends UnaryExpression {
    public NotOp(Expression argument) {
        super(argument);
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
