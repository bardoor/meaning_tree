package org.vstu.meaningtree;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Identifier;

public class NamespaceResolving extends Expression {
    protected final Expression expr;
    protected final Identifier member;

    public NamespaceResolving(Expression expr, Identifier member) {
        this.expr = expr;
        this.member = member;
    }

    public Expression getExpression() {
        return expr;
    }

    public Identifier getMember() {
        return member;
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
