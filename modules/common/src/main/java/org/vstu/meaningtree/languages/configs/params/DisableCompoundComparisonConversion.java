package org.vstu.meaningtree.languages.configs.params;

import org.vstu.meaningtree.languages.configs.ConfigParameter;
import org.vstu.meaningtree.languages.configs.ConfigScope;

public class DisableCompoundComparisonConversion extends ConfigParameter<Boolean> {
    public static final String name = "disableCompoundComparisonConversion";

    public DisableCompoundComparisonConversion(Boolean value, ConfigScope scope) {
        super(name, value, scope);
    }

    public DisableCompoundComparisonConversion(Boolean value) {
        this(value, ConfigScope.ANY);
    }
}
