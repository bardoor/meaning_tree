package org.vstu.meaningtree.languages;

import org.vstu.meaningtree.languages.configs.ConfigParameter;

import java.util.HashMap;
import java.util.Map;

public class PythonTranslator extends LanguageTranslator {
    public PythonTranslator(Map<String, String> rawStringConfig) {
        super(new PythonLanguage(), new PythonViewer(), rawStringConfig);
    }

    public PythonTranslator() {
        super(new PythonLanguage(), new PythonViewer(), new HashMap<>());
    }

    @Override
    protected ConfigParameter[] getDeclaredConfigParameters() {
        return new ConfigParameter[0];
    }
}
