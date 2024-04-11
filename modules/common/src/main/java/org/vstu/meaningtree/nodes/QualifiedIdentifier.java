package org.vstu.meaningtree.nodes;

public class QualifiedIdentifier extends Expression {
    private final ScopedIdentifier _scope;
    private final Identifier _member;

    public QualifiedIdentifier(ScopedIdentifier scope, Identifier member) {
        _scope = scope;
        _member = member;
    }

    public ScopedIdentifier getScope() {
        return _scope;
    }

    public Identifier getMember() {
        return _member;
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
