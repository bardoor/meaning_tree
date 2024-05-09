package org.vstu.meaningtree.nodes;

import org.vstu.meaningtree.nodes.identifiers.ScopedIdentifier;

import java.util.Optional;

public class Import extends Node {
    public enum ImportType {
        LIBRARY,
        LIBRARY_STATIC, // example: import static in java
        SINGLE_FILE // in C++
    }

    private final ImportType _type;
    private final Identifier _scope;
    private final Optional<Identifier> _member;

    private final Optional<Identifier> _alias;

    public Import(ImportType type, Identifier scope, Identifier member, Identifier alias) {
        this._scope = scope;
        this._member = Optional.ofNullable(member);
        this._type = type;
        this._alias = Optional.ofNullable(alias);
    }

    @Override
    public String generateDot() {
        return "";
    }

    public ImportType getType() {
        return _type;
    }

    public Identifier getScope() {
        return _scope;
    }

    public Identifier getMember() {
        if (!hasMember()) {
            throw new RuntimeException("Member isn't present");
        }
        return _member.get();
    }

    public boolean hasAlias() {
        return _alias.isPresent();
    }

    public boolean hasMember() {
        return _member.isPresent();
    }

    public Identifier getAlias() {
        if (!hasAlias()) {
            throw new RuntimeException("Alias isn't present");
        }
        return _alias.get();
    }

    public boolean isAllImport() {
        return hasMember() && _member.get() == ScopedIdentifier.ALL;
    }
}
