package org.vstu.meaningtree.languages.configs.params;

import org.vstu.meaningtree.languages.configs.ConfigParameter;
import org.vstu.meaningtree.languages.configs.ConfigScope;

public class EnforceEntryPoint extends ConfigParameter<Boolean> {
    public static final String name = "enforceEntryPoint";

    public EnforceEntryPoint(Boolean value, ConfigScope scope) {
        super(name, value, scope);
    }

    public EnforceEntryPoint(Boolean value) {
        this(value, ConfigScope.ANY);
    }
}
