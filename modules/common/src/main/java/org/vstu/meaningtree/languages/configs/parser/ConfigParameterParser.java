package org.vstu.meaningtree.languages.configs.parser;

import org.jetbrains.annotations.NotNull;
import org.vstu.meaningtree.languages.configs.ConfigParameter;

import java.util.Optional;

@FunctionalInterface
public interface ConfigParameterParser {
    Optional<ConfigParameter<?>> parse(@NotNull String value);
}
