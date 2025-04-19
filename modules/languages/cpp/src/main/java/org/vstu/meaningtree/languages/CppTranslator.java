package org.vstu.meaningtree.languages;

import org.vstu.meaningtree.languages.configs.ConfigParameter;
import org.vstu.meaningtree.utils.tokens.Token;
import org.vstu.meaningtree.utils.tokens.TokenList;
import org.vstu.meaningtree.utils.tokens.TokenType;

import java.util.HashMap;
import java.util.Map;

public class CppTranslator extends LanguageTranslator {
    public CppTranslator(Map<String, String> rawConfig) {
        super(new CppLanguage(), null, rawConfig);
        this.setViewer(new CppViewer(this));
    }

    public CppTranslator() {
        super(new CppLanguage(), null, new HashMap<>());
        this.setViewer(new CppViewer(this));
    }

    @Override
    public LanguageTokenizer getTokenizer() {
        return new CppTokenizer(this);
    }

    @Override
    public String prepareCode(String code) {
        if (getConfigParameter("expressionMode").getBooleanValue() && !code.endsWith(";")) {
            code = code + ";";
        }
        if (getConfigParameter("expressionMode").getBooleanValue()) {
            code = String.format("int main() {%s}", code);
        }
        return code;
    }

    @Override
    public TokenList prepareCode(TokenList list) {
        if (getConfigParameter("expressionMode").getBooleanValue() && !list.getLast().type.equals(TokenType.SEPARATOR)) {
            list.add(new Token(";", TokenType.SEPARATOR));
        }
        if (getConfigParameter("expressionMode").getBooleanValue()) {
            TokenList final_ = getTokenizer().tokenize("int main() {}", false);
            final_.addAll(
                    final_.indexOf(
                            final_.stream().filter((Token t) -> t.value.equals("{")).findFirst().orElse(null)
                    ),
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
