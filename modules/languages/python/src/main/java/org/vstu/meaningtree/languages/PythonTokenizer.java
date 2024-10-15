package org.vstu.meaningtree.languages;

import org.treesitter.TSNode;
import org.vstu.meaningtree.utils.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PythonTokenizer extends LanguageTokenizer {
    private final Map<String, OperatorToken> operators = new HashMap<>() {{
        put("CALL_(", new OperatorToken("(", TokenType.CALL_OPENING_BRACE, 2, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));
        put("CALL_)", new OperatorToken(")", TokenType.CALL_CLOSING_BRACE, 2, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));
        put("[", new OperatorToken("[", TokenType.SUBSCRIPT_OPENING_BRACE, 2, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));
        put("]", new OperatorToken("[", TokenType.SUBSCRIPT_CLOSING_BRACE, 2, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));
        put(".", new OperatorToken(".", TokenType.OPERATOR, 2, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));
        put("**", new OperatorToken("**", TokenType.OPERATOR, 4, OperatorAssociativity.RIGHT, OperatorArity.BINARY, false)); // Возведение в степень
        put("~", new OperatorToken("~", TokenType.OPERATOR, 5, OperatorAssociativity.LEFT, OperatorArity.UNARY, false)); // Побитовая инверсия
        put("UPLUS", new OperatorToken("+", TokenType.OPERATOR, 5, OperatorAssociativity.LEFT, OperatorArity.UNARY, false)); // Унарный плюс
        put("UMINUS", new OperatorToken("-", TokenType.OPERATOR, 5, OperatorAssociativity.LEFT, OperatorArity.UNARY, false)); // Унарный минус
        put("*", new OperatorToken("*", TokenType.OPERATOR, 6, OperatorAssociativity.LEFT, OperatorArity.BINARY, false)); // Умножение
        put("/", new OperatorToken("/", TokenType.OPERATOR, 6, OperatorAssociativity.LEFT, OperatorArity.BINARY, false)); // Деление
        put("//", new OperatorToken("//", TokenType.OPERATOR, 6, OperatorAssociativity.LEFT, OperatorArity.BINARY, false)); // Целочисленное деление
        put("%", new OperatorToken("%", TokenType.OPERATOR, 6, OperatorAssociativity.LEFT, OperatorArity.BINARY, false)); // Остаток от деления
        put("@", new OperatorToken("@", TokenType.OPERATOR, 6, OperatorAssociativity.LEFT, OperatorArity.BINARY, false)); // Матричное умножение
        put("+", new OperatorToken("+", TokenType.OPERATOR, 7, OperatorAssociativity.LEFT, OperatorArity.BINARY, false)); // Сложение
        put("-", new OperatorToken("-", TokenType.OPERATOR, 7, OperatorAssociativity.LEFT, OperatorArity.BINARY, false)); // Вычитание
        put("<<", new OperatorToken("<<", TokenType.OPERATOR, 8, OperatorAssociativity.LEFT, OperatorArity.BINARY, false)); // Левый сдвиг
        put(">>", new OperatorToken(">>", TokenType.OPERATOR, 8, OperatorAssociativity.LEFT, OperatorArity.BINARY, false)); // Правый сдвиг
        put("&", new OperatorToken("&", TokenType.OPERATOR, 9, OperatorAssociativity.LEFT, OperatorArity.BINARY, false)); // Побитовое И
        put("^", new OperatorToken("^", TokenType.OPERATOR, 10, OperatorAssociativity.LEFT, OperatorArity.BINARY, false)); // Побитовое исключающее ИЛИ
        put("|", new OperatorToken("|", TokenType.OPERATOR, 11, OperatorAssociativity.LEFT, OperatorArity.BINARY, false)); // Побитовое ИЛИ
        put("<", new OperatorToken("<", TokenType.OPERATOR, 12, OperatorAssociativity.LEFT, OperatorArity.BINARY, false)); // Меньше
        put("<=", new OperatorToken("<=", TokenType.OPERATOR, 12, OperatorAssociativity.LEFT, OperatorArity.BINARY, false)); // Меньше или равно
        put(">", new OperatorToken(">", TokenType.OPERATOR, 12, OperatorAssociativity.LEFT, OperatorArity.BINARY, false)); // Больше
        put(">=", new OperatorToken(">=", TokenType.OPERATOR, 12, OperatorAssociativity.LEFT, OperatorArity.BINARY, false)); // Больше или равно
        put("==", new OperatorToken("==", TokenType.OPERATOR, 12, OperatorAssociativity.LEFT, OperatorArity.BINARY, false)); // Равно
        put("!=", new OperatorToken("!=", TokenType.OPERATOR, 12, OperatorAssociativity.LEFT, OperatorArity.BINARY, false)); // Не равно
        put("in", new OperatorToken("in", TokenType.OPERATOR, 12, OperatorAssociativity.LEFT, OperatorArity.BINARY, false)); // Оператор in
        put("is", new OperatorToken("is", TokenType.OPERATOR, 12, OperatorAssociativity.LEFT, OperatorArity.BINARY, false)); // Оператор is
        put("not", new OperatorToken("not", TokenType.OPERATOR, 13, OperatorAssociativity.RIGHT, OperatorArity.UNARY, false)); // Логическое НЕ
        put("and", new OperatorToken("and", TokenType.OPERATOR, 14, OperatorAssociativity.LEFT, OperatorArity.BINARY, true)); // Логическое И
        put("or", new OperatorToken("or", TokenType.OPERATOR, 15, OperatorAssociativity.LEFT, OperatorArity.BINARY, true)); // Логическое ИЛИ
        put("if", new OperatorToken("if", TokenType.KEYWORD, 16, OperatorAssociativity.RIGHT, OperatorArity.BINARY, true)); // Условные выражения
        put("else", new OperatorToken("else", TokenType.KEYWORD, 16, OperatorAssociativity.RIGHT, OperatorArity.BINARY, true)); // Условные выражения
        put("lambda", new OperatorToken("lambda", TokenType.KEYWORD, 17, OperatorAssociativity.RIGHT, OperatorArity.UNARY, false)); // Лямбда-выражения
        put(":=", new OperatorToken(":=", TokenType.OPERATOR, 18, OperatorAssociativity.LEFT, OperatorArity.BINARY, false)); // Моржовый оператор
    }};


    public PythonTokenizer(String code, PythonLanguage parser) {
        super(code, parser);
    }

    @Override
    protected String[] getStopNodes() {
        return new String[] {"string"};
    }

    @Override
    protected OperatorToken getOperator(String tokenValue, TSNode node) {
        if (node.getParent().getType().equals("unary_operator") && List.of("+", "-").contains(tokenValue)) {
            if (tokenValue.equals("+")) {
                return operators.get("UPLUS");
            } else if (tokenValue.equals("-")) {
                return operators.get("UMINUS");
            }
        }
        if (tokenValue.equals("(") && !node.getParent().isNull() && node.getParent().getType().equals("call")) {
            return operators.get("CALL_(");
        }

        if (tokenValue.equals(")") && !node.getParent().isNull() && node.getParent().getType().equals("call")) {
            return operators.get("CALL_)");
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

        TokenType tokenType;

        if (recognizedOperator != null
                && !((type.equals("[") || type.equals("]")) && parent.getType().equals("list"))) {
            return recognizedOperator;
        }

        if (type.equals("{") && List.of("set", "dictionary").contains(parent.getType())) {
            tokenType = TokenType.INITIALIZER_LIST_OPENING_BRACE;
        } else if (type.equals("}") && List.of("set", "dictionary").contains(parent.getType())) {
            tokenType = TokenType.INITIALIZER_LIST_CLOSING_BRACE;
        } else if (type.equals("(") && parent.getType().equals("tuple")) {
            tokenType = TokenType.INITIALIZER_LIST_OPENING_BRACE;
        } else if (type.equals(")") && parent.getType().equals("tuple")) {
            tokenType = TokenType.INITIALIZER_LIST_CLOSING_BRACE;
        } else if (type.equals("(")) {
            tokenType = TokenType.OPENING_BRACE;
        } else if (type.equals(")")) {
            tokenType = TokenType.CLOSING_BRACE;
        } else if (List.of(";", ":").contains(type)) {
            tokenType = TokenType.SEPARATOR;
        } else if (List.of("type_identifier", "identifier",
                "integral_type", "boolean_type").contains(type)) {
            if (!parent.isNull() && parent.getType().equals("call")) {
                tokenType = TokenType.CALLABLE_IDENTIFIER;
            } else {
                tokenType = TokenType.IDENTIFIER;
            }
        } else if (List.of("integer", "float",
                "string", "true", "false", "none").contains(type)) {
            tokenType = TokenType.CONST;
        } else if (type.equals(",")) {
            tokenType = TokenType.COMMA;
        } else if (List.of("lambda", "if", "else").contains(type)) {
            tokenType = TokenType.KEYWORD;
        } else if (List.of("=", "+=", "-=", "*=", "/=", "//=", "%=", "&=", "|=", "^=", ">>=", "<<=").contains(type)) {
            tokenType = TokenType.STATEMENT_TOKEN;
        } else if (type.equals("[") && parent.getType().equals("list")) {
            tokenType = TokenType.INITIALIZER_LIST_OPENING_BRACE;
        } else if (type.equals("]") && parent.getType().equals("list")) {
            tokenType = TokenType.INITIALIZER_LIST_CLOSING_BRACE;
        }
        else {
            tokenType = TokenType.UNKNOWN;
        }
        return new Token(TreeSitterUtils.getCodePiece(code, node), tokenType);
    }
}
