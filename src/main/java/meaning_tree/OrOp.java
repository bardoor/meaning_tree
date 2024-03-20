package meaning_tree;

public class OrOp extends BinaryExpression {
    public OrOp(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}