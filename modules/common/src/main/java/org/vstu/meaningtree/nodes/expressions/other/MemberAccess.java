package org.vstu.meaningtree.nodes.expressions.other;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.expressions.identifiers.ScopedIdentifier;
import org.vstu.meaningtree.nodes.expressions.identifiers.SimpleIdentifier;

import java.util.*;

public class MemberAccess extends Expression {
    protected final Expression _expr;
    protected final SimpleIdentifier _member;

    public MemberAccess(Expression expr, SimpleIdentifier member) {
        this._expr = expr;
        this._member = member;
    }

    public Expression getExpression() {
        return _expr;
    }

    public SimpleIdentifier getMember() {
        return _member;
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }

    public ScopedIdentifier toScopedIdentifier() {
        List<SimpleIdentifier> idents = new ArrayList<>();
        idents.add(_member);
        _unwrapScoped(_expr, idents);
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
                List<SimpleIdentifier> idents = scoped.getScopeResolution();
                Collections.reverse(idents);
                list.addAll(idents);
            }
            case null, default ->
                    throw new UnsupportedOperationException("Member access cannot be converted to scoped identifier, not identifier node found");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MemberAccess that = (MemberAccess) o;
        return Objects.equals(_expr, that._expr) && Objects.equals(_member, that._member);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_expr, _member);
    }
}
