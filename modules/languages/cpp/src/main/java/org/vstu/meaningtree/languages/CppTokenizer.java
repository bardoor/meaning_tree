package org.vstu.meaningtree.languages;

import org.treesitter.TSNode;
import org.vstu.meaningtree.utils.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CppTokenizer extends LanguageTokenizer {
    private static final Map<String, OperatorToken> operators = new HashMap<>() {{
        put("::", new OperatorToken("::", TokenType.OPERATOR, 1, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));
        put("CALL_(", new OperatorToken("(", TokenType.CALL_OPENING_BRACE, 2, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));
        put("CALL_)", new OperatorToken(")", TokenType.CALL_CLOSING_BRACE, 2, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));
        put("[", new OperatorToken("[", TokenType.SUBSCRIPT_OPENING_BRACE, 2, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));
        put("]", new OperatorToken("[", TokenType.SUBSCRIPT_CLOSING_BRACE, 2, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));
        put("->", new OperatorToken("->", TokenType.OPERATOR, 2, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));
        put(".", new OperatorToken(".", TokenType.OPERATOR, 2, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));
        put("++", new OperatorToken("++", TokenType.OPERATOR, 2, OperatorAssociativity.LEFT, OperatorArity.UNARY, false));
        put("--", new OperatorToken("--", TokenType.OPERATOR, 2, OperatorAssociativity.LEFT, OperatorArity.UNARY, false));
        put("++U", new OperatorToken("++", TokenType.OPERATOR, 3, OperatorAssociativity.RIGHT, OperatorArity.UNARY, false));
        put("--U", new OperatorToken("--", TokenType.OPERATOR, 3, OperatorAssociativity.RIGHT, OperatorArity.UNARY, false));
        put("UMINUS", new OperatorToken("-", TokenType.OPERATOR, 3, OperatorAssociativity.RIGHT, OperatorArity.UNARY, false));
        put("UPLUS", new OperatorToken("+", TokenType.OPERATOR, 3, OperatorAssociativity.RIGHT, OperatorArity.UNARY, false));
        put("POINTER_&", new OperatorToken("&", TokenType.OPERATOR, 3, OperatorAssociativity.RIGHT, OperatorArity.UNARY, false));
        put("POINTER_*", new OperatorToken("*", TokenType.OPERATOR, 3, OperatorAssociativity.RIGHT, OperatorArity.UNARY, false));
        put("~", new OperatorToken("~", TokenType.OPERATOR, 3, OperatorAssociativity.RIGHT, OperatorArity.UNARY, false));
        put("!", new OperatorToken("!", TokenType.OPERATOR, 3, OperatorAssociativity.RIGHT, OperatorArity.UNARY, false));
        put("sizeof", new OperatorToken("sizeof", TokenType.OPERATOR, 3, OperatorAssociativity.RIGHT, OperatorArity.UNARY, false));
        put("new", new OperatorToken("new", TokenType.OPERATOR, 3, OperatorAssociativity.RIGHT, OperatorArity.UNARY, false));
        put("delete", new OperatorToken("delete", TokenType.OPERATOR, 3, OperatorAssociativity.RIGHT, OperatorArity.UNARY, false));

        put("*", new OperatorToken("*", TokenType.OPERATOR, 5, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));
        put("/", new OperatorToken("/", TokenType.OPERATOR, 5, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));
        put("%", new OperatorToken("%", TokenType.OPERATOR, 5, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));

        put("+", new OperatorToken("+", TokenType.OPERATOR, 6, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));
        put("-", new OperatorToken("-", TokenType.OPERATOR, 6, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));

        put("<<", new OperatorToken("<<", TokenType.OPERATOR, 7, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));
        put(">>", new OperatorToken(">>", TokenType.OPERATOR, 7, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));

        put("<", new OperatorToken("<", TokenType.OPERATOR, 8, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));
        put("<=", new OperatorToken("<=", TokenType.OPERATOR, 8, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));
        put(">", new OperatorToken(">", TokenType.OPERATOR, 8, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));
        put(">=", new OperatorToken(">=", TokenType.OPERATOR, 8, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));

        put("==", new OperatorToken("==", TokenType.OPERATOR, 9, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));
        put("!=", new OperatorToken("!=", TokenType.OPERATOR, 9, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));

        put("&", new OperatorToken("&", TokenType.OPERATOR, 10, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));

        put("^", new OperatorToken("^", TokenType.OPERATOR, 11, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));

        put("|", new OperatorToken("|", TokenType.OPERATOR, 12, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));

        put("&&", new OperatorToken("&&", TokenType.OPERATOR, 13, OperatorAssociativity.LEFT, OperatorArity.BINARY, true));

        put("||", new OperatorToken("||", TokenType.OPERATOR, 14, OperatorAssociativity.LEFT, OperatorArity.BINARY, true));

        put("?", new OperatorToken("?", TokenType.OPERATOR, 15, OperatorAssociativity.RIGHT, OperatorArity.TERNARY, true));
        put(":", new OperatorToken(":", TokenType.OPERATOR, 15, OperatorAssociativity.RIGHT, OperatorArity.TERNARY, true));

        put("=", new OperatorToken("=", TokenType.OPERATOR, 16, OperatorAssociativity.RIGHT, OperatorArity.BINARY, false));
        put("+=", new OperatorToken("+=", TokenType.OPERATOR, 16, OperatorAssociativity.RIGHT, OperatorArity.BINARY, false));
        put("-=", new OperatorToken("-=", TokenType.OPERATOR, 16, OperatorAssociativity.RIGHT, OperatorArity.BINARY, false));
        put("*=", new OperatorToken("*=", TokenType.OPERATOR, 16, OperatorAssociativity.RIGHT, OperatorArity.BINARY, false));
        put("/=", new OperatorToken("/=", TokenType.OPERATOR, 16, OperatorAssociativity.RIGHT, OperatorArity.BINARY, false));
        put("%=", new OperatorToken("%=", TokenType.OPERATOR, 16, OperatorAssociativity.RIGHT, OperatorArity.BINARY, false));
        put("<<=", new OperatorToken("<<=", TokenType.OPERATOR, 16, OperatorAssociativity.RIGHT, OperatorArity.BINARY, false));
        put(">>=", new OperatorToken(">>=", TokenType.OPERATOR, 16, OperatorAssociativity.RIGHT, OperatorArity.BINARY, false));
        put("&=", new OperatorToken("&=", TokenType.OPERATOR, 16, OperatorAssociativity.RIGHT, OperatorArity.BINARY, false));
        put("^=", new OperatorToken("^=", TokenType.OPERATOR, 16, OperatorAssociativity.RIGHT, OperatorArity.BINARY, false));
        put("|=", new OperatorToken("|=", TokenType.OPERATOR, 16, OperatorAssociativity.RIGHT, OperatorArity.BINARY, false));

        put(",", new OperatorToken(",", TokenType.COMMA, 17, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));
    }};


    public CppTokenizer(String code, CppLanguage parser) {
        super(code, parser);
    }

    @Override
    protected String[] getStopNodes() {
        return new String[] {"user_defined_literal"};
    }

    protected OperatorToken getOperator(String tokenValue, TSNode node) {
        if (node.getParent().getType().equals("unary_expression") && List.of("+", "-").contains(tokenValue)) {
            if (tokenValue.equals("+")) {
                return operators.get("UPLUS");
            } else if (tokenValue.equals("-")) {
                return operators.get("UMINUS");
            }
        }

        if (tokenValue.equals("(") && !node.getParent().isNull() && node.getParent().getType().equals("call_expression")) {
            return operators.get("CALL_(");
        }

        if (tokenValue.equals(")") && !node.getParent().isNull() && node.getParent().getType().equals("call_expression")) {
            return operators.get("CALL_)");
        }

        if (node.getParent().getType().equals("update_expression") &&
                TreeSitterUtils.getCodePiece(code, node.getParent()).startsWith("++") && tokenValue.equals("++")) {
            return operators.get("++U");
        }

        if (node.getParent().getType().equals("update_expression") &&
                TreeSitterUtils.getCodePiece(code, node.getParent()).startsWith("--") && tokenValue.equals("--")) {
            return operators.get("--U");
        }

        if (node.getParent().getType().equals("pointer_expression") && List.of("*", "&").contains(tokenValue)) {
            if (tokenValue.equals("*")) {
                return operators.get("POINTER_*");
            } else if (tokenValue.equals("&")) {
                return operators.get("POINTER_&");
            }
        }

        return operators.getOrDefault(tokenValue, null);
    }

    @Override
    public OperatorToken getOperatorByTokenName(String tokenName) {
        return operators.getOrDefault(tokenName, null);
    }


    @Override
    protected Token recognizeToken(TSNode node) {
        String type = node.getType();
        TSNode parent = node.getParent();
        OperatorToken recognizedOperator = getOperator(type, node);

        if (recognizedOperator != null) {
            return recognizedOperator;
        }

        TokenType tokenType;

        if (type.equals("{")) {
            if (parent.getType().equals("initializer_list")) {
                tokenType = TokenType.INITIALIZER_LIST_OPENING_BRACE;
            } else {
                tokenType = TokenType.COMPOUND_OPENING_BRACE;
            }
        } else if (type.equals("}")) {
            if (parent.getType().equals("initializer_list")) {
                tokenType = TokenType.INITIALIZER_LIST_CLOSING_BRACE;
            } else {
                tokenType = TokenType.COMPOUND_CLOSING_BRACE;
            }
        } else if (type.equals("(")) {
            tokenType = TokenType.OPENING_BRACE;
        } else if (type.equals(")")) {
            tokenType = TokenType.CLOSING_BRACE;
        } else if (type.equals(";")) {
            tokenType = TokenType.SEPARATOR;
        } else if (List.of("identifier", "namespace_identifier", "type_identifier", "field_identifier").contains(type)) {
            if (!parent.isNull() && parent.getType().equals("call_expression")) {
                tokenType = TokenType.CALLABLE_IDENTIFIER;
            } else {
                tokenType = TokenType.IDENTIFIER;
            }
        } else if (List.of("number_literal", "string_literal", "true", "false", "null", "user_defined_literal").contains(type)) {
            tokenType = TokenType.CONST;
        } else {
            tokenType = TokenType.UNKNOWN;
        }
        return new Token(TreeSitterUtils.getCodePiece(code, node), tokenType);
    }
}
