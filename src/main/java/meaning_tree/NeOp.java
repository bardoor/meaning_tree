package meaning_tree;

public class NeOp extends BinaryExpression {
    public NeOp(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}