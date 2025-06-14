package org.vstu.meaningtree.languages.configs;

import java.util.Arrays;

public abstract class ConfigParameter<T> {
    protected T _value;

    protected ConfigParameter(T value) {
        _value = value;
    }

    public abstract String getName();
    public T getValue() { return _value; }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) { return true; }
        if (!(obj instanceof ConfigParameter<?> other)) { return false; }
        return getName().equals(other.getName());
    }

}
