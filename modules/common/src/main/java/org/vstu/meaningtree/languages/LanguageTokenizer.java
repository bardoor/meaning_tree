package org.vstu.meaningtree.languages;

import org.treesitter.TSNode;
import org.vstu.meaningtree.utils.OperatorAssociativity;
import org.vstu.meaningtree.utils.OperatorToken;
import org.vstu.meaningtree.utils.Token;
import org.vstu.meaningtree.utils.TreeSitterUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class LanguageTokenizer {
    protected String code;
    protected LanguageParser parser;

    protected abstract Token recognizeToken(TSNode node);

    public List<Token> tokenize() {
        ArrayList<Token> list = new ArrayList<>();
        collectTokens(parser.getRootNode(), list);
        return list;
    }

    /**
     * Список узлов tree sitter дерева, внутрь которых при обходе заходить не нужно, но нужно их обработать целиком
     * @return
     */
    protected String[] getStopNodes() {
        return new String[0];
    }

    protected abstract OperatorToken getOperator(String tokenValue, TSNode node);
    protected abstract OperatorToken getOperatorByTokenName(String tokenName);

    public LanguageTokenizer(String code, LanguageParser parser) {
        this.code = code;
        this.parser = parser;
    }

    protected OperatorAssociativity getOperatorAssociativity(String tokenValue, TSNode node) {
        OperatorToken token = getOperator(tokenValue, node);
        if (token != null) {
            return token.assoc;
        }
        return null;
    }

    protected int getOperatorPrecedence(String tokenValue, TSNode node) {
        OperatorToken token = getOperator(tokenValue, node);
        if (token != null) {
            return token.precedence;
        }
        return -1;
    }

    protected void collectTokens(TSNode node, List<Token> tokens) {
        if (node.getChildCount() == 0 || List.of(getStopNodes()).contains(node.getType())) {
            String value = TreeSitterUtils.getCodePiece(code, node);
            if (value.trim().isEmpty()) {
                return;
            }
            tokens.add(recognizeToken(node));
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            collectTokens(node.getChild(i), tokens);
        }
    }
}
