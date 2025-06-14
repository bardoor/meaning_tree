package org.vstu.meaningtree.languages.configs.params;

import org.vstu.meaningtree.languages.configs.ConfigScopedParameter;
import org.vstu.meaningtree.languages.configs.ConfigScope;

/**
 * Конфигурационный параметр, управляющий режимом обработки ошибок.
 * <p>
 * Если значение {@code true}, то при ошибках все равно попытаться построить дерево.
 * Если значение {@code false}, то при ошибках трансляция будет прервана.
 * <p>
 */
public class SkipErrors extends ConfigScopedParameter<Boolean> {
    public static final String name = "skipErrors";

    public String getName() { return name; }

    public SkipErrors(Boolean value, ConfigScope scope) {
        super(value, scope);
    }

    public SkipErrors(Boolean value) {
        this(value, ConfigScope.ANY);
    }

}
