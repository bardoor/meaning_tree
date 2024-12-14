package org.vstu.meaningtree.nodes.expressions.identifiers;

import org.vstu.meaningtree.nodes.expressions.Identifier;

import java.util.Objects;

public class QualifiedIdentifier extends Identifier {
    private Identifier _scope;
    private SimpleIdentifier _member;

    public QualifiedIdentifier(Identifier scope, SimpleIdentifier member) {
        _scope = scope;
        _member = member;
    }

    public Identifier getScope() {
        return _scope;
    }

    public SimpleIdentifier getMember() {
        return _member;
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QualifiedIdentifier that = (QualifiedIdentifier) o;
        return Objects.equals(_scope, that._scope) && Objects.equals(_member, that._member);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), _scope, _member);
    }

    @Override
    public QualifiedIdentifier clone() {
        QualifiedIdentifier obj = (QualifiedIdentifier) super.clone();
        obj._scope = _scope.clone();
        obj._member = _member.clone();
        return obj;
    }

    @Override
    public boolean contains(Identifier other) {
        return _scope.contains(other) || _member.equals(other);
    }

    @Override
    public int contentSize() {
        return _scope.contentSize() + 1;
    }
}
