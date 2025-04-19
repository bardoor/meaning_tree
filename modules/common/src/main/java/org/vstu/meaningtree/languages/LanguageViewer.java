package org.vstu.meaningtree.languages;

import org.vstu.meaningtree.MeaningTree;
import org.vstu.meaningtree.languages.configs.ConfigParameter;
import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Node;
import org.vstu.meaningtree.utils.ParenthesesFiller;
import org.vstu.meaningtree.utils.tokens.OperatorToken;

import java.util.List;

abstract public class LanguageViewer {
    private List<ConfigParameter> _cfg;
    protected MeaningTree origin;

    public LanguageViewer(LanguageTranslator translator) {
        this.translator = translator;
        this.parenFiller = new ParenthesesFiller(this::mapToToken);
    }

    protected LanguageTranslator translator;
    protected ParenthesesFiller parenFiller;

    public abstract String toString(Node node);
    public abstract OperatorToken mapToToken(Expression expr);

    public String toString(MeaningTree mt) {
        origin = mt;
        return toString(mt.getRootNode());
    }

    void setConfig(List<ConfigParameter> params) {
        _cfg = params;
    }

    protected ConfigParameter getConfigParameter(String paramName) {
        for (ConfigParameter param : _cfg) {
            if (param.getName().equals(paramName)) {
                return param;
            }
        }
        return null;
    }
}
