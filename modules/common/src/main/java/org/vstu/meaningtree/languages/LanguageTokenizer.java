package org.vstu.meaningtree.languages;

import org.treesitter.TSNode;
import org.vstu.meaningtree.utils.*;

import java.util.ArrayList;
import java.util.List;

public abstract class LanguageTokenizer {
    protected String code;
    protected LanguageParser parser;

    protected abstract TokenType recognizeTokenType(TSNode node);

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

    protected abstract int getOperatorPrecedence(String tokenValue, TSNode node);

    protected abstract OperatorAssociativity getOperatorAssociativity(String tokenValue, TSNode node);

    public LanguageTokenizer(String code, LanguageParser parser) {
        this.code = code;
        this.parser = parser;
    }

    protected void collectTokens(TSNode node, List<Token> tokens) {
        if (node.getChildCount() == 0 || List.of(getStopNodes()).contains(node.getType())) {
            TokenType type = recognizeTokenType(node);
            String value = TreeSitterUtils.getCodePiece(code, node);
            if (getOperatorPrecedence(value, node) != -1) {
                tokens.add(new OperatorToken(value, getOperatorPrecedence(value, node), getOperatorAssociativity(value, node)));
            } else {
                tokens.add(new Token(TreeSitterUtils.getCodePiece(code, node), type));
            }
            return;
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            collectTokens(node.getChild(i), tokens);
        }
    }
}
