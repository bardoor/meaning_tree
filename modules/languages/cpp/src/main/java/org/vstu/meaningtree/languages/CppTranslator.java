package org.vstu.meaningtree.languages;

import org.vstu.meaningtree.languages.configs.ConfigParameter;
import org.vstu.meaningtree.utils.tokens.Token;
import org.vstu.meaningtree.utils.tokens.TokenList;
import org.vstu.meaningtree.utils.tokens.TokenType;

import java.util.HashMap;
import java.util.Map;

public class CppTranslator extends LanguageTranslator {
    public CppTranslator(Map<String, String> rawConfig) {
        super(new CppLanguage(), new CppViewer(), rawConfig);
    }

    public CppTranslator() {
        super(new CppLanguage(), new CppViewer(), new HashMap<>());
    }

    @Override
    public LanguageTokenizer getTokenizer() {
        return new CppTokenizer((CppLanguage) _language, (CppViewer) _viewer);
    }

    @Override
    public String prepareCode(String code) {
        if (getConfigParameter("expressionMode").getBooleanValue() && !code.endsWith(";")) {
            code = code + ";";
        }
        return code;
    }

    @Override
    public TokenList prepareCode(TokenList list) {
        if (getConfigParameter("expressionMode").getBooleanValue() && !list.getLast().type.equals(TokenType.SEPARATOR)) {
            list.add(new Token(";", TokenType.SEPARATOR));
        }
        return list;
    }

    @Override
    protected ConfigParameter[] getDeclaredConfigParameters() {
        return new ConfigParameter[0];
    }
}
