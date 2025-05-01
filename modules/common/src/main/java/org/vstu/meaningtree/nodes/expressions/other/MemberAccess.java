package org.vstu.meaningtree.nodes.expressions.other;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.expressions.identifiers.ScopedIdentifier;
import org.vstu.meaningtree.nodes.expressions.identifiers.SimpleIdentifier;
import org.vstu.meaningtree.utils.TreeNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MemberAccess extends Expression {
    @TreeNode protected Expression expr;
    @TreeNode protected SimpleIdentifier member;

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

    public ScopedIdentifier toScopedIdentifier() {
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
        return Objects.equals(expr, that.expr) && Objects.equals(member, that.member);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), expr, member);
    }

    @Override
    public MemberAccess clone() {
        MemberAccess obj = (MemberAccess) super.clone();
        obj.expr = expr.clone();
        obj.member = member.clone();
        return obj;
    }
}
