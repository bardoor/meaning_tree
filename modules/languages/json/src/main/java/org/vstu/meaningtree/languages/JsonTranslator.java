package org.vstu.meaningtree.languages;

import org.vstu.meaningtree.languages.configs.ConfigParameter;
import org.vstu.meaningtree.utils.tokens.TokenList;

import java.util.HashMap;
import java.util.Map;

public class JsonTranslator extends LanguageTranslator {
    public JsonTranslator(Map<String, String> rawConfig) {
        super(new JsonLanguage(), new JsonViewer(), rawConfig);
    }

    public JsonTranslator() {
        super(new JsonLanguage(), new JsonViewer(), new HashMap<String, String>());
    }

    protected JsonTranslator(LanguageParser language, LanguageViewer viewer, Map<String, String> rawConfig) {
        super(language, viewer, rawConfig);
    }

    @Override
    public LanguageTokenizer getTokenizer() {
        return null;
    }

    @Override
    public String prepareCode(String code) {
        return "";
    }

    @Override
    public TokenList prepareCode(TokenList list) {
        return null;
    }

    @Override
    protected ConfigParameter[] getDeclaredConfigParameters() {
        return new ConfigParameter[0];
    }
}
