package org.vstu.meaningtree.languages;

import org.vstu.meaningtree.languages.configs.Config;
import org.vstu.meaningtree.languages.configs.ConfigParameter;
import org.vstu.meaningtree.languages.configs.ConfigScope;
import org.vstu.meaningtree.languages.configs.params.DisableCompoundComparisonConversion;
import org.vstu.meaningtree.utils.tokens.TokenList;

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
    protected Config getDeclaredConfig() {
        return new Config(new DisableCompoundComparisonConversion(false, ConfigScope.TRANSLATOR));
    }
}
