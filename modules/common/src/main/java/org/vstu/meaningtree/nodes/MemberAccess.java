package org.vstu.meaningtree.nodes;

import org.vstu.meaningtree.nodes.identifiers.ScopedIdentifier;
import org.vstu.meaningtree.nodes.identifiers.SimpleIdentifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

    public Node toScopedIdentifier() {
        List<SimpleIdentifier> idents = new ArrayList<>();
        idents.add(member);
        _unwrapScoped(expr, idents);
        Collections.reverse(idents);
        return new ScopedIdentifier(idents.toArray(new SimpleIdentifier[0]));
    }

    private void _unwrapScoped(Expression expr, List<SimpleIdentifier> list) {
        switch (expr) {
            case SimpleIdentifier simple -> list.add(simple);
            case MemberAccess memberAccess -> {
                list.add(memberAccess.getMember());
                _unwrapScoped(memberAccess.getExpression(), list);
            }
            case ScopedIdentifier scoped -> {
                List<SimpleIdentifier> idents = Arrays.asList(scoped.getScopeResolution());
                Collections.reverse(idents);
                list.addAll(idents);
            }
            case null, default ->
                    throw new UnsupportedOperationException("Member access cannot be converted to scoped identifier, not identifier node found");
        }
    }
}
