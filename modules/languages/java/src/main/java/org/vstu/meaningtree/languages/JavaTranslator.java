package org.vstu.meaningtree.languages;

import org.vstu.meaningtree.languages.configs.ConfigParameter;

import java.util.HashMap;

public class JavaTranslator extends LanguageTranslator {
    public JavaTranslator(HashMap<String, String> rawConfig) {
        super(new JavaLanguage(), new JavaViewer(), rawConfig);
    }

    public JavaTranslator() {
        super(new JavaLanguage(), new JavaViewer(), new HashMap<>());
    }

    @Override
    protected ConfigParameter[] getDeclaredConfigParameters() {
        return new ConfigParameter[0];
    }
}
