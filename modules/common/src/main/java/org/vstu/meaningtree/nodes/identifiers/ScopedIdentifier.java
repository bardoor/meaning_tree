package org.vstu.meaningtree.nodes.identifiers;

import org.vstu.meaningtree.nodes.Identifier;
import org.vstu.meaningtree.nodes.utils.WildcardImport;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ScopedIdentifier extends Identifier {
    private final List<SimpleIdentifier> _scopeResolutionList;

    public static final ScopedIdentifier ALL = new ScopedIdentifier(new WildcardImport());

    public ScopedIdentifier(SimpleIdentifier... identifiers) {
        _scopeResolutionList = List.of(identifiers);
    }

    public ScopedIdentifier(List<SimpleIdentifier> identifiers) {
        _scopeResolutionList = List.copyOf(identifiers);
    }

    public List<SimpleIdentifier> getScopeResolution() {
        return _scopeResolutionList;
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return _scopeResolutionList.stream().map(SimpleIdentifier::toString).collect(Collectors.joining("."));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScopedIdentifier that = (ScopedIdentifier) o;
        return Objects.equals(_scopeResolutionList, that._scopeResolutionList);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(_scopeResolutionList);
    }
}
