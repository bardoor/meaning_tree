package org.vstu.meaningtree.languages.configs;

import java.util.Arrays;

public abstract class ConfigParameter<T> {
    protected String _name;
    protected T _value;
    protected ConfigScope _scope;

    protected ConfigParameter(String name, T value, ConfigScope scope) {
        this(value, scope);
        _name = name;
    }

    public ConfigParameter(T value) {
        this(value, ConfigScope.ANY);
    }

    public ConfigParameter(T value, ConfigScope scope) {
        _value = value;
        _scope = scope;
    }

    public String getName() { return _name; }
    public T getValue() { return _value; }

    public boolean inScope(ConfigScope scope) {
        return _scope == ConfigScope.ANY || _scope == scope;
    }

    public boolean inAnyScope(ConfigScope ...scopes) {
        return Arrays.stream(scopes).anyMatch(this::inScope);
    }

    @Override
    public int hashCode() {
        return _name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) { return true; }
        if (!(obj instanceof ConfigParameter<?> other)) { return false; }
        return _name.equals(other._name);
    }
}
