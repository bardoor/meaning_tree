package org.vstu.meaningtree.languages.configs;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class Config {
    private final Map<Class<?>, ConfigParameter<?>> parameters = new HashMap<>();

    public Config(ConfigParameter<?> ...configParameters) {
        put(configParameters);
    }

    public Config(Iterable<ConfigParameter<?>> configParameters) {
        put(configParameters);
    }

    public void put(Iterable<ConfigParameter<?>> configParameters) {
        for (var param : configParameters) {
            put(param);
        }
    }

    public void put(ConfigParameter<?> ...configParameters) {
        for (var param : configParameters) {
            put(param);
        }
    }

    public void put(ConfigParameter<?> parameter) {
        parameters.put(parameter.getClass(), parameter);
    }

    public <P, T extends ConfigParameter<P>> boolean has(Class<T> paramClass) {
        return parameters.containsKey(paramClass);
    }

    @SuppressWarnings({"unchecked"})
    public <T extends ConfigParameter<?>> void putNew(T parameter) {
        if (!has(parameter.getClass())) {
            put(parameter);
        }
    }

    public void merge(Config other) {
        for (var param : other.parameters.values()) {
            put(param);
        }
    }

    public void merge(Config ...others) {
        for (var other : others) {
            merge(other);
        }
    }

    public Config subset(Predicate<ConfigParameter<?>> predicate) {
        return new Config(
                parameters.values().stream().filter(predicate).toList()
        );
    }

    // Приношу свои извинения за эту параметрическую жуть, но зато можно делать
    // config.get(ExpressionMode.class) - и будет статическая проверка типов с обработкой Optional
    public <P, T extends ConfigParameter<P>> Optional<P> get(Class<T> paramClass) {
        return Optional.ofNullable(parameters.get(paramClass))
                .filter(paramClass::isInstance)
                .map(paramClass::cast)
                .map(ConfigParameter::getValue);
    }
}
