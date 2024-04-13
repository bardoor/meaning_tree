package org.vstu.meaningtree.nodes;

import org.vstu.meaningtree.nodes.identifiers.SimpleIdentifier;

public class MemberAccess extends Expression {
    protected final Expression expr;
    protected final SimpleIdentifier member;

    public MemberAccess(Expression expr, SimpleIdentifier member) {
        this.expr = expr;
        this.member = member;
    }

    public Expression getExpression() {
        return expr;
    }

    public SimpleIdentifier getMember() {
        return member;
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
