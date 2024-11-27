package org.vstu.meaningtree.languages;

import org.treesitter.TSNode;
import org.vstu.meaningtree.MeaningTree;
import org.vstu.meaningtree.nodes.Node;
import org.vstu.meaningtree.utils.TreeSitterUtils;
import org.vstu.meaningtree.utils.tokens.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class LanguageTokenizer {
    protected String code;
    protected LanguageTranslator translator;
    protected LanguageParser parser;
    protected LanguageViewer viewer;

    protected abstract Token recognizeToken(TSNode node);

    /**
     * Токенизирует выражения из кода, переданного в токенайзер
     * @return
     */
    public TokenList tokenize(String code) {
        this.code = translator.prepareCode(code);
        parser.getMeaningTree(this.code);
        TokenList list = new TokenList();
        collectTokens(parser.getRootNode(), list, true);
        return list;
    }

    public abstract TokenList tokenizeExtended(Node node);

    /**
     * Токенизирует узлы из дерева MeaningTree, с возможностью выведения привязанных к узлам значений
     * На данный момент расширенные токены генерируются только для выражений (используйте в конфигурации языка expressionMode=true и skipErrors=true)
     * @param mt - общее дерево
     * @return
     */
    public TokenList tokenizeExtended(MeaningTree mt) {
        return tokenizeExtended(mt.getRootNode());
    }

    public TokenList tokenizeExtended(String code) {
        return tokenizeExtended(parser.getMeaningTree(code));
    }

    /**
     * Список узлов tree sitter дерева, внутрь которых при обходе заходить не нужно, но нужно их обработать целиком
     * @return
     */
    protected List<String> getStopNodes() {
        return List.of();
    }

    protected abstract List<String> getOperatorNodes(OperatorArity arity);

    protected abstract String getFieldNameByOperandPos(OperandPosition pos, String operatorNode);

    protected abstract OperatorToken getOperator(String tokenValue, TSNode node);
    public abstract OperatorToken getOperatorByTokenName(String tokenName);

    public LanguageTokenizer(LanguageTranslator translator) {
        this.translator = translator;
        this.parser = translator._language;
        this.viewer = translator._viewer;
    }

    protected TokenGroup collectTokens(TSNode node, TokenList tokens, boolean detectOperator) {
        int start = tokens.size();
        boolean skipChildren = false;
        if (node.getChildCount() == 0 || getStopNodes().contains(node.getType())) {
            String value = TreeSitterUtils.getCodePiece(code, node);
            if (value.trim().isEmpty()) {
                return null;
            }
            tokens.add(recognizeToken(node));
        } else if (
                (getOperatorNodes(OperatorArity.BINARY).contains(node.getType())
                || getOperatorNodes(OperatorArity.TERNARY).contains(node.getType())) && detectOperator
        ) {
            OperatorToken token = null;
            Map<OperandPosition, TokenGroup> operands = new HashMap<>();
            for (int i = 0; i < node.getChildCount(); i++) {
                TokenGroup group = collectTokens(node.getChild(i), tokens, true);
                if (node.getFieldNameForChild(i) != null) {
                    if (node.getFieldNameForChild(i).equals(getFieldNameByOperandPos(OperandPosition.LEFT, node.getType()))) {
                        operands.put(OperandPosition.LEFT, group);
                    } else if (node.getFieldNameForChild(i).equals(getFieldNameByOperandPos(OperandPosition.RIGHT, node.getType()))) {
                        operands.put(OperandPosition.RIGHT, group);
                    } else if (node.getFieldNameForChild(i).equals(getFieldNameByOperandPos(OperandPosition.CENTER, node.getType()))) {
                        operands.put(OperandPosition.CENTER, group);
                    }
                }
                if (group.length() == 1 && tokens.get(group.start) instanceof OperatorToken op && token == null) {
                    token = op;
                }
            }

            for (OperandPosition pos : operands.keySet()) {
                TokenGroup group = operands.get(pos);
                for (int i = group.start; i < group.stop; i++) {
                    if (!(tokens.get(i) instanceof OperandToken)) {
                        tokens.set(i, new OperandToken(tokens.get(i).value, tokens.get(i).type));
                    }
                    OperandToken opTok = (OperandToken)tokens.get(i);
                    if (opTok.operandOf() == null) {
                        opTok.setMetadata(token, pos);
                    }
                }
            }
            skipChildren = true;
        } else if (getOperatorNodes(OperatorArity.UNARY).contains(node.getType()) && detectOperator) {
            TokenGroup group = collectTokens(node, tokens, false);
            int unaryStart = group.start;
            int unaryStop = group.stop;
            OperatorToken token;
            OperandPosition pos = OperandPosition.RIGHT;
            if (tokens.get(unaryStart) instanceof OperatorToken op) {
                token = op;
                unaryStart++;
            } else {
                token = (OperatorToken) tokens.getLast();
                unaryStop--;
                pos = OperandPosition.LEFT;
            }
            for (int i = unaryStart; i < unaryStop; i++) {
                if (!(tokens.get(i) instanceof OperandToken)) {
                    tokens.set(i, new OperandToken(tokens.get(i).value, tokens.get(i).type));
                }
                ((OperandToken)tokens.get(i)).setMetadata(token, pos);
            }
            skipChildren = true;
        }
        for (int i = 0; i < node.getChildCount() && !skipChildren; i++) {
            collectTokens(node.getChild(i), tokens, true);
        }
        int stop = tokens.size();
        return new TokenGroup(start, stop, tokens);

    }
}
