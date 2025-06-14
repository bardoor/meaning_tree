package org.vstu.meaningtree.languages;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.treesitter.TSException;
import org.treesitter.TSNode;
import org.vstu.meaningtree.MeaningTree;
import org.vstu.meaningtree.exceptions.MeaningTreeException;
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


    public TokenList tokenize(String code, boolean noPrepare) {
        this.code = noPrepare ? code : translator.prepareCode(code);
        parser.getMeaningTree(this.code);
        TokenList list = new TokenList();
        collectTokens(parser.getRootNode(), list, true, null);
        return list;
    }

    public Pair<Boolean, TokenList> tryTokenize(String code, boolean noPrepare) {
        try {
            return ImmutablePair.of(true, tokenize(code, noPrepare));
        } catch (TSException | MeaningTreeException | IllegalArgumentException | ClassCastException e) {
            return ImmutablePair.of(false, null);
        }
    }

    /**
     * Токенизирует выражения из кода, переданного в токенайзер
     * Не учитывает режим выражения и прочие конфигурации
     * @return
     */
    public TokenList tokenize(String code) {
        return tokenize(code, false);
    }

    public Pair<Boolean, TokenList> tryTokenize(String code) {
        try {
            return ImmutablePair.of(true, tokenize(code));
        } catch (TSException | MeaningTreeException | IllegalArgumentException | ClassCastException e) {
            return ImmutablePair.of(false, null);
        }
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

    public Pair<Boolean, TokenList> tryTokenizeExtended(MeaningTree mt) {
        try {
            return ImmutablePair.of(true, tokenizeExtended(mt));
        } catch (TSException | MeaningTreeException | IllegalArgumentException | ClassCastException e) {
            return ImmutablePair.of(false, null);
        }
    }

    public TokenList tokenizeExtended(String code) {
        return tokenizeExtended(parser.getMeaningTree(translator.prepareCode(code)));
    }

    public Pair<Boolean, TokenList> tryTokenizeExtended(String code) {
        try {
            return ImmutablePair.of(true, tokenizeExtended(code));
        } catch (TSException | MeaningTreeException | IllegalArgumentException | ClassCastException e) {
            return ImmutablePair.of(false, null);
        }
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

    protected TokenGroup collectTokens(TSNode node, TokenList tokens, boolean detectOperator, Map<OperandPosition, TokenGroup> parent) {
        int start = tokens.size();
        boolean skipChildren = false;
        if (node.getChildCount() == 0 || getStopNodes().contains(node.getType())) {
            String value = TreeSitterUtils.getCodePiece(code, node);
            if (value.trim().isEmpty()) {
                return new TokenGroup(0, 0, tokens);
            }
            tokens.add(recognizeToken(node));
            skipChildren = true;
        } else if (
                (getOperatorNodes(OperatorArity.BINARY).contains(node.getType())
                || getOperatorNodes(OperatorArity.TERNARY).contains(node.getType())) && detectOperator
        ) {
            OperatorToken token = null;
            Map<OperandPosition, TokenGroup> operands = new HashMap<>();
            for (int i = 0; i < node.getChildCount(); i++) {
                TokenGroup group = collectTokens(node.getChild(i), tokens, true, operands);

                String leftName = getFieldNameByOperandPos(OperandPosition.LEFT, node.getType());
                String rightName = getFieldNameByOperandPos(OperandPosition.RIGHT, node.getType());
                String centerName = getFieldNameByOperandPos(OperandPosition.CENTER, node.getType());

                if (!node.getParent().isNull() && parent != null) {
                    String leftParentName = getFieldNameByOperandPos(OperandPosition.LEFT, node.getParent().getType());
                    String rightParentName = getFieldNameByOperandPos(OperandPosition.RIGHT, node.getParent().getType());
                    String centerParentName = getFieldNameByOperandPos(OperandPosition.CENTER, node.getParent().getType());

                    if (leftParentName != null && leftParentName.contains(".")
                            && leftParentName.split("\\.").length == 2
                            && leftParentName.split("\\.")[1].equals(node.getType())
                    ) {
                        parent.put(OperandPosition.LEFT, group);
                    } else if (rightParentName != null && rightParentName.contains(".")
                            && rightParentName.split("\\.").length == 2
                            && rightParentName.split("\\.")[1].equals(node.getType())
                    ) {
                        parent.put(OperandPosition.RIGHT, group);
                    } else if (centerParentName != null && centerParentName.contains(".")
                            && centerParentName.split("\\.").length == 2
                            && centerParentName.split("\\.")[1].equals(node.getType())
                    ) {
                        parent.put(OperandPosition.CENTER, group);
                    }
                }

                int namedIndex = -1;
                for (int j = 0; j < node.getNamedChildCount(); j++) {
                    if (node.getChild(i).equals(node.getNamedChild(j))) {
                        namedIndex = j;
                        break;
                    }
                }
                String index = "_" + namedIndex;
                OperandPosition pos = null;

                if (index.equals(leftName)) {
                    pos = OperandPosition.LEFT;
                } else if (index.equals(centerName)) {
                    pos = OperandPosition.CENTER;
                } else if (index.equals(rightName)) {
                    pos = OperandPosition.RIGHT;
                }

                if (node.getFieldNameForChild(i) != null) {
                    if (node.getFieldNameForChild(i).equals(leftName)) {
                        pos = OperandPosition.LEFT;
                    } else if (node.getFieldNameForChild(i).equals(rightName)) {
                        pos = OperandPosition.RIGHT;
                    } else if (node.getFieldNameForChild(i).equals(centerName)) {
                        pos = OperandPosition.CENTER;
                    }
                }

                if (pos != null) {
                    operands.put(pos, group);
                }

                if (group.length() > 0 && group.length() == 1 && tokens.get(group.start) instanceof OperatorToken op && token == null) {
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
            TokenGroup group = collectTokens(node, tokens, false, null);
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
            collectTokens(node.getChild(i), tokens, true, null);
        }
        int stop = tokens.size();
        return new TokenGroup(start, stop, tokens);

    }
}
