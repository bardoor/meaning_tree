package org.vstu.meaningtree.languages.configs.params;

import org.vstu.meaningtree.languages.configs.ConfigParameter;
import org.vstu.meaningtree.languages.configs.ConfigScopedParameter;
import org.vstu.meaningtree.languages.configs.ConfigScope;
import org.vstu.meaningtree.languages.configs.parser.BooleanParser;
import org.vstu.meaningtree.languages.configs.parser.ConfigParameterParser;

import java.util.Optional;

public class DisableCompoundComparisonConversion extends ConfigScopedParameter<Boolean> {
    public DisableCompoundComparisonConversion(Boolean value, ConfigScope scope) {
        super(value, scope);
    }

    public DisableCompoundComparisonConversion(Boolean value) {
        this(value, ConfigScope.ANY);
    }

    public static Optional<Boolean> parse(String value) {
        return BooleanParser.parse(value);
    }
}
