package org.vstu.meaningtree.languages.configs.params;

import org.vstu.meaningtree.languages.configs.ConfigParameter;
import org.vstu.meaningtree.languages.configs.ConfigScope;

/**
 * Конфигурационный параметр, управляющий режимом вывода программы.
 * <p>
 * Если значение {@code true}, то при генерации кода будет выведено только одно выражение.
 * Если значение {@code false}, то будет выведена полная программа.
 * <p>
 */
public class ExpressionMode extends ConfigParameter<Boolean> {
    public static final String name = "expressionMode";

    public ExpressionMode(Boolean value, ConfigScope scope) {
        super(name, value, scope);
    }

    public ExpressionMode(Boolean value) {
        this(value, ConfigScope.ANY);
    }

}
