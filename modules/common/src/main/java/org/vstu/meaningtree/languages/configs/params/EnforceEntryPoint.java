package org.vstu.meaningtree.languages.configs.params;

import org.vstu.meaningtree.languages.configs.ConfigScopedParameter;
import org.vstu.meaningtree.languages.configs.ConfigScope;

public class EnforceEntryPoint extends ConfigScopedParameter<Boolean> {
    public static final String name = "enforceEntryPoint";

    public String getName() { return name; }

    public EnforceEntryPoint(Boolean value, ConfigScope scope) {
        super(value, scope);
    }

    public EnforceEntryPoint(Boolean value) {
        this(value, ConfigScope.ANY);
    }
}
