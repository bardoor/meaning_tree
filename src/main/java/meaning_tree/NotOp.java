package meaning_tree;

public class NotOp extends UnaryExpression {
    public NotOp(Expression argument) {
        super(argument);
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
