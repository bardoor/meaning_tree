package org.vstu.meaningtree.languages;

import org.vstu.meaningtree.languages.configs.ConfigParameter;
import org.vstu.meaningtree.utils.tokens.Token;
import org.vstu.meaningtree.utils.tokens.TokenList;
import org.vstu.meaningtree.utils.tokens.TokenType;

import java.util.HashMap;
import java.util.Map;

public class JavaTranslator extends LanguageTranslator {
    public JavaTranslator(Map<String, String> rawConfig) {
        super(new JavaLanguage(), new JavaViewer(), rawConfig);
    }

    public JavaTranslator() {
        super(new JavaLanguage(), new JavaViewer(), new HashMap<>());
    }

    @Override
    public LanguageTokenizer getTokenizer() {
        return new JavaTokenizer(this);
    }

    @Override
    public String prepareCode(String code) {
        if (getConfigParameter("expressionMode").getBooleanValue() && !code.endsWith(";")) {
            code = code + ";";
        }
        if (getConfigParameter("expressionMode").getBooleanValue()) {
            code = String.format("class Main { public static void main(String[] args) {%s} }", code);
        }
        return code;
    }

    @Override
    public TokenList prepareCode(TokenList list) {
        if (getConfigParameter("expressionMode").getBooleanValue() && !list.getLast().type.equals(TokenType.SEPARATOR)) {
            list.add(new Token(";", TokenType.SEPARATOR));
        }
        if (getConfigParameter("expressionMode").getBooleanValue()) {
            TokenList final_ = getTokenizer().tokenize("class Main { public static void main(String[] args) {;%s} }");
            int marker = final_.indexOf(
                    final_.stream().filter((Token t) -> t.value.equals(";")).findFirst().orElse(null)
            );
            final_.remove(marker);
            final_.addAll(
                    marker,
                    list
            );
            return final_;
        }
        return list;
    }

    @Override
    protected ConfigParameter[] getDeclaredConfigParameters() {
        return new ConfigParameter[0];
    }
}
