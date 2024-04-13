package org.vstu.meaningtree.nodes.identifiers;

import org.vstu.meaningtree.nodes.Expression;

public class QualifiedIdentifier extends Expression {
    private final ScopedIdentifier _scope;
    private final SimpleIdentifier _member;

    public QualifiedIdentifier(ScopedIdentifier scope, SimpleIdentifier member) {
        _scope = scope;
        _member = member;
    }

    public ScopedIdentifier getScope() {
        return _scope;
    }

    public SimpleIdentifier getMember() {
        return _member;
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
