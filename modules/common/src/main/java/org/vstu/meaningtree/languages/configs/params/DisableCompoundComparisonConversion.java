package org.vstu.meaningtree.languages.configs.params;

import org.vstu.meaningtree.languages.configs.ConfigScopedParameter;
import org.vstu.meaningtree.languages.configs.ConfigScope;

public class DisableCompoundComparisonConversion extends ConfigScopedParameter<Boolean> {
    public static final String name = "disableCompoundComparisonConversion";

    public String getName() { return name; }

    public DisableCompoundComparisonConversion(Boolean value, ConfigScope scope) {
        super(value, scope);
    }

    public DisableCompoundComparisonConversion(Boolean value) {
        this(value, ConfigScope.ANY);
    }
}
