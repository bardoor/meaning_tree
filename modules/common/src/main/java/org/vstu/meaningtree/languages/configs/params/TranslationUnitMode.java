package org.vstu.meaningtree.languages.configs.params;

import org.vstu.meaningtree.languages.configs.ConfigParameter;
import org.vstu.meaningtree.languages.configs.ConfigScope;

/**
 * Конфигурационный параметр, управляющий режимом вывода программы.
 * <p>
 * Если значение {@code true}, то при генерации кода будет выведен полный текст программы,
 * Если значение {@code false}, то может быть выведена только часть программы — например, одно выражение.
 * <p>
 */
public class TranslationUnitMode extends ConfigParameter<Boolean> {
    public static final String name = "translationUnitMode";

    public TranslationUnitMode(Boolean value, ConfigScope scope) {
        super(name, value, scope);
    }

    public TranslationUnitMode(Boolean value) {
        this(value, ConfigScope.ANY);
    }

}
