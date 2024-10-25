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
    public LanguageTokenizer getTokenizer() {
        return new PythonTokenizer((PythonLanguage) _language, (PythonViewer) _viewer);
    }

    @Override
    protected ConfigParameter[] getDeclaredConfigParameters() {
        return new ConfigParameter[] {
                // Отключает распознавание составных сравнений и их преобразование из нескольких соединенных "И"
                new ConfigParameter("disableCompoundComparisonConversion", false, ConfigParameter.Scope.PARSER)
        };
    }
}
