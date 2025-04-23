package org.vstu.meaningtree.languages;

import org.vstu.meaningtree.languages.configs.ConfigParameter;
import org.vstu.meaningtree.utils.tokens.TokenList;

import java.util.HashMap;
import java.util.Map;

public class PythonTranslator extends LanguageTranslator {
    public static final int ID = 1;

    public PythonTranslator(Map<String, String> rawStringConfig) {
        super(new PythonLanguage(), null, rawStringConfig);
        this.setViewer(new PythonViewer(this));
    }

    public PythonTranslator() {
        super(new PythonLanguage(), null, new HashMap<>());
        this.setViewer(new PythonViewer(this));
    }

    @Override
    public int getLanguageId() {
        return ID;
    }

    @Override
    public LanguageTokenizer getTokenizer() {
        return new PythonTokenizer(this);
    }

    @Override
    public String prepareCode(String code) {
        return code;
    }

    @Override
    public TokenList prepareCode(TokenList list) {
        return list;
    }

    @Override
    protected ConfigParameter[] getDeclaredConfigParameters() {
        return new ConfigParameter[] {
                // Отключает распознавание составных сравнений и их преобразование из нескольких соединенных "И"
                new ConfigParameter("disableCompoundComparisonConversion", false, ConfigParameter.Scope.TRANSLATOR)
        };
    }
}
