package meaning_tree;

public class EqOp extends BinaryExpression {
    public EqOp(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
