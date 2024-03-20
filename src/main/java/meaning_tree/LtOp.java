package meaning_tree;

public class LtOp extends BinaryExpression {
    public LtOp(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
