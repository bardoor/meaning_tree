package org.vstu.meaningtree.nodes;

public class Import extends Node {
    public enum Type {
        LIBRARY,
        LIBRARY_STATIC, // example: import static in java
        SINGLE_FILE // in C++
    }

    private final Type _type;
    private final ScopedIdentifier _scope;
    private final ScopedIdentifier _member;

    public Import(Type type, ScopedIdentifier scope, ScopedIdentifier member) {
        this._type = type;
        this._scope = scope;
        this._member = member;
    }

    @Override
    public String generateDot() {
        return "";
    }

    public ScopedIdentifier getScope() {
        return _scope;
    }

    public ScopedIdentifier getMember() {
        return _member;
    }

    public Type getType() {
        return _type;
    }

    public boolean isAllImport() {
        return _member == ScopedIdentifier.ALL;
    }
}
