package meaning_tree;

public class MulOp extends BinaryExpression {
    public MulOp(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
