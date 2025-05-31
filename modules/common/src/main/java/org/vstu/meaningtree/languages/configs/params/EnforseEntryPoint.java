package org.vstu.meaningtree.languages.configs.params;

import org.vstu.meaningtree.languages.configs.ConfigParameter;
import org.vstu.meaningtree.languages.configs.ConfigScope;

public class EnforseEntryPoint extends ConfigParameter<Boolean> {
    public static final String name = "enforseEntryPoint";

    public EnforseEntryPoint(Boolean value, ConfigScope scope) {
        super(name, value, scope);
    }

    public EnforseEntryPoint(Boolean value) {
        this(value, ConfigScope.ANY);
    }
}
