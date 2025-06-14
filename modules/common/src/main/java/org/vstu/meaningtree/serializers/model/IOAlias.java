package org.vstu.meaningtree.serializers.model;

import java.util.Optional;
import java.util.function.Function;

public class IOAlias<T> {
    private final String name;
    private final T value;

    public IOAlias(String name, T value) {
        this.name = name;
        this.value = value;
    }

    public <R> Optional<R> apply(String requestedName, Function<T, R> function) {
        if (this.name.equalsIgnoreCase(requestedName)) {
            return Optional.of(function.apply(value));
        }
        return Optional.empty();
    }

    public String getNameForDisplay() {
        return name;
    }
} 