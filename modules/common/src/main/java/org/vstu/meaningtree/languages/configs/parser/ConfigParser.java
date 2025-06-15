package org.vstu.meaningtree.languages.configs.parser;

import org.vstu.meaningtree.exceptions.UnsupportedConfigParameterException;
import org.vstu.meaningtree.languages.configs.ConfigParameter;

import java.util.HashMap;
import java.util.Map;

public class ConfigParser {
    private final Map<String, ConfigMapping<?>> configMappings = new HashMap<>();

    public ConfigParser(ConfigMapping<?>... mappings) {
        for (var mapping : mappings) {
            configMappings.put(mapping.name(), mapping);
        }
    }

    public ConfigParameter<?> parse(String key, String value) {
        var mapping = configMappings.get(key);

        if (mapping == null) {
            throw new UnsupportedConfigParameterException("Parameter '" + key + "' is not supported");
        }

        return mapping.createParameter(value);
    }

}
