package org.vstu.meaningtree.languages.configs;

import org.vstu.meaningtree.exceptions.UnsupportedConfigParameterException;
import org.vstu.meaningtree.languages.configs.params.*;

public class ConfigParser {
    public ConfigParameter<?> parse(String key, String val) {
        return switch (key) {
            case ExpressionMode.name -> new ExpressionMode(Boolean.parseBoolean(val));
            case TranslationUnitMode.name -> new TranslationUnitMode(Boolean.parseBoolean(val));
            case SkipErrors.name -> new SkipErrors(Boolean.parseBoolean(val));
            case DisableCompoundComparisonConversion.name -> new DisableCompoundComparisonConversion(Boolean.parseBoolean(val));
            case EnforceEntryPoint.name -> new EnforceEntryPoint(Boolean.parseBoolean(val));
            default -> throw new UnsupportedConfigParameterException(key);
        };
    }

}
