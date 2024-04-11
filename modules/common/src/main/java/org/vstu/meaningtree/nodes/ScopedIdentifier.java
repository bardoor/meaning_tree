package org.vstu.meaningtree.nodes;

import org.vstu.meaningtree.nodes.utils.WildcardImport;

import java.util.List;

public class ScopedIdentifier extends Expression {
    private final List<Identifier> _scopeResolutionList;

    public static final ScopedIdentifier ALL = new ScopedIdentifier(new WildcardImport());


    public ScopedIdentifier(Identifier ... identifiers) {
        _scopeResolutionList = List.of(identifiers);
    }

    public Identifier[] getScopeResolution() {
        return _scopeResolutionList.toArray(new Identifier[0]);
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
