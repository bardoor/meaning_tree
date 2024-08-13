package org.vstu.meaningtree.nodes.types;

import org.vstu.meaningtree.nodes.identifiers.Identifier;
import org.vstu.meaningtree.nodes.Type;
import org.vstu.meaningtree.nodes.identifiers.QualifiedIdentifier;
import org.vstu.meaningtree.nodes.identifiers.ScopedIdentifier;
import org.vstu.meaningtree.nodes.identifiers.SimpleIdentifier;

import java.util.Objects;

public abstract class UserType extends Type {
    private final Identifier _name;

    public SimpleIdentifier getName() {
        if (getQualifiedName() instanceof QualifiedIdentifier qualified) {
            return qualified.getMember();
        } else if (getQualifiedName() instanceof ScopedIdentifier scoped) {
            return scoped.getScopeResolution().getLast();
        }
        return (SimpleIdentifier) _name;
    }

    // Например для вывода имени вместе с namespace. Может быть QualifiedIdentifier в случае C++, либо ScopedIdentifier в случае Java и Python
    public Identifier getQualifiedName() {
        return _name;
    }

    protected UserType(Identifier name) {
        _name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        UserType other = (UserType) o;
        return getName().equals(other.getName());
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), _name);
    }
}
