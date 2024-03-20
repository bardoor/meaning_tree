package meaning_tree;

public class DivOp extends BinaryExpression {
    public DivOp(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
