package org.vstu.meaningtree.nodes.types;

import org.vstu.meaningtree.nodes.Identifier;
import org.vstu.meaningtree.nodes.Type;
import org.vstu.meaningtree.nodes.identifiers.QualifiedIdentifier;
import org.vstu.meaningtree.nodes.identifiers.ScopedIdentifier;
import org.vstu.meaningtree.nodes.identifiers.SimpleIdentifier;

import java.util.Optional;

public class UserType extends Type {
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


    public UserType(Identifier name) {
        _name = name;
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
