package org.vstu.meaningtree.languages;

import org.vstu.meaningtree.languages.configs.ConfigParameter;

import java.util.HashMap;

public class CppTranslator extends LanguageTranslator {
    public CppTranslator(HashMap<String, String> rawConfig) {
        super(new CppLanguage(), new CppViewer(), rawConfig);
    }

    public CppTranslator() {
        super(new CppLanguage(), new CppViewer(), new HashMap<>());
    }

    @Override
    protected ConfigParameter[] getDeclaredConfigParameters() {
        return new ConfigParameter[0];
    }
}
