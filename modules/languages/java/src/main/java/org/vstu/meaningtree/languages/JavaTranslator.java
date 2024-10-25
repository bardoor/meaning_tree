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
    public LanguageTokenizer getTokenizer() {
        return new JavaTokenizer((JavaLanguage) _language, (JavaViewer) _viewer);
    }

    @Override
    protected ConfigParameter[] getDeclaredConfigParameters() {
        return new ConfigParameter[0];
    }
}
