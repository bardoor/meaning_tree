package meaning_tree;

public class GtOp extends BinaryExpression {
    public GtOp(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
