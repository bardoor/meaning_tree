package org.vstu.meaningtree.nodes.identifiers;

import org.vstu.meaningtree.nodes.Identifier;
import org.vstu.meaningtree.nodes.utils.WildcardImport;

import java.util.List;
import java.util.stream.Collectors;

public class ScopedIdentifier extends Identifier {
    private final List<SimpleIdentifier> _scopeResolutionList;

    public static final ScopedIdentifier ALL = new ScopedIdentifier(new WildcardImport());

    public ScopedIdentifier(SimpleIdentifier... identifiers) {
        _scopeResolutionList = List.of(identifiers);
    }

    public SimpleIdentifier[] getScopeResolution() {
        return _scopeResolutionList.toArray(new SimpleIdentifier[0]);
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return _scopeResolutionList.stream().map(SimpleIdentifier::toString).collect(Collectors.joining("."));
    }
}
