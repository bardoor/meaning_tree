package org.vstu.meaningtree.languages;

import org.treesitter.TSNode;
import org.vstu.meaningtree.utils.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JavaTokenizer extends LanguageTokenizer {
    private static final Map<String, OperatorToken> operators = new HashMap<>() {{
        put("CALL_(", new OperatorToken("(", TokenType.CALL_OPEN_BRACE, 1, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));
        put("CALL_)", new OperatorToken(")", TokenType.CALL_CLOSE_BRACE, 1, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));
        put("[", new OperatorToken("[", TokenType.SUBSCRIPT_OPEN_BRACE, 1, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));
        put("]", new OperatorToken("[", TokenType.SUBSCRIPT_CLOSE_BRACE, 1, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));

        put("++", new OperatorToken("++", TokenType.OPERATOR, 2, OperatorAssociativity.LEFT, OperatorArity.UNARY, false));   // Постфиксный инкремент
        put("--", new OperatorToken("--", TokenType.OPERATOR, 2, OperatorAssociativity.LEFT, OperatorArity.UNARY, false));   // Постфиксный декремент

        put("+U", new OperatorToken("+", TokenType.OPERATOR, 3, OperatorAssociativity.RIGHT, OperatorArity.UNARY, false));   // Унарный плюс
        put("-U", new OperatorToken("-", TokenType.OPERATOR, 3, OperatorAssociativity.RIGHT, OperatorArity.UNARY, false));   // Унарный минус
        put("++U", new OperatorToken("++", TokenType.OPERATOR, 3, OperatorAssociativity.RIGHT, OperatorArity.UNARY, false)); // Префиксный инкремент
        put("--U", new OperatorToken("--", TokenType.OPERATOR, 3, OperatorAssociativity.RIGHT, OperatorArity.UNARY, false)); // Префиксный декремент
        put("!", new OperatorToken("!", TokenType.OPERATOR, 3, OperatorAssociativity.RIGHT, OperatorArity.UNARY, false));     // Логическое НЕ
        put("~", new OperatorToken("~", TokenType.OPERATOR, 3, OperatorAssociativity.RIGHT, OperatorArity.UNARY, false));     // Побитовая инверсия

        put("*", new OperatorToken("*", TokenType.OPERATOR, 4, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));     // Умножение
        put("/", new OperatorToken("/", TokenType.OPERATOR, 4, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));     // Деление
        put("%", new OperatorToken("%", TokenType.OPERATOR, 4, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));     // Остаток от деления

        put("+", new OperatorToken("+", TokenType.OPERATOR, 5, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));     // Сложение (бинарный)
        put("-", new OperatorToken("-", TokenType.OPERATOR, 5, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));     // Вычитание (бинарный)

        put("<<", new OperatorToken("<<", TokenType.OPERATOR, 6, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));   // Левый сдвиг
        put(">>", new OperatorToken(">>", TokenType.OPERATOR, 6, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));   // Правый сдвиг
        put(">>>", new OperatorToken(">>>", TokenType.OPERATOR, 6, OperatorAssociativity.LEFT, OperatorArity.BINARY, false)); // Беззнаковый правый сдвиг

        put("<", new OperatorToken("<", TokenType.OPERATOR, 7, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));     // Меньше
        put("<=", new OperatorToken("<=", TokenType.OPERATOR, 7, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));   // Меньше или равно
        put(">", new OperatorToken(">", TokenType.OPERATOR, 7, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));     // Больше
        put(">=", new OperatorToken(">=", TokenType.OPERATOR, 7, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));   // Больше или равно
        put("instanceof", new OperatorToken("instanceof", TokenType.OPERATOR, 7, OperatorAssociativity.LEFT, OperatorArity.BINARY, false)); // Проверка на тип

        put("==", new OperatorToken("==", TokenType.OPERATOR, 8, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));   // Равно
        put("!=", new OperatorToken("!=", TokenType.OPERATOR, 8, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));   // Не равно

        put("&", new OperatorToken("&", TokenType.OPERATOR, 9, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));     // Побитовое И
        put("^", new OperatorToken("^", TokenType.OPERATOR, 10, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));    // Побитовое исключающее ИЛИ
        put("|", new OperatorToken("|", TokenType.OPERATOR, 11, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));    // Побитовое ИЛИ

        put("&&", new OperatorToken("&&", TokenType.OPERATOR, 12, OperatorAssociativity.LEFT, OperatorArity.BINARY, true));  // Логическое И
        put("||", new OperatorToken("||", TokenType.OPERATOR, 13, OperatorAssociativity.LEFT, OperatorArity.BINARY, true));  // Логическое ИЛИ

        put("?", new OperatorToken("?", TokenType.OPERATOR, 14, OperatorAssociativity.RIGHT, OperatorArity.TERNARY, true));  // Тернарный оператор
        put(":", new OperatorToken(":", TokenType.OPERATOR, 14, OperatorAssociativity.RIGHT, OperatorArity.TERNARY, true));

        put("=", new OperatorToken("=", TokenType.OPERATOR, 15, OperatorAssociativity.RIGHT, OperatorArity.BINARY, false));   // Присваивание
        put("+=", new OperatorToken("+=", TokenType.OPERATOR, 15, OperatorAssociativity.RIGHT, OperatorArity.BINARY, false)); // Сложение с присваиванием
        put("-=", new OperatorToken("-=", TokenType.OPERATOR, 15, OperatorAssociativity.RIGHT, OperatorArity.BINARY, false)); // Вычитание с присваиванием
        put("*=", new OperatorToken("*=", TokenType.OPERATOR, 15, OperatorAssociativity.RIGHT, OperatorArity.BINARY, false)); // Умножение с присваиванием
        put("/=", new OperatorToken("/=", TokenType.OPERATOR, 15, OperatorAssociativity.RIGHT, OperatorArity.BINARY, false)); // Деление с присваиванием
        put("%=", new OperatorToken("%=", TokenType.OPERATOR, 15, OperatorAssociativity.RIGHT, OperatorArity.BINARY, false)); // Остаток с присваиванием
        put("<<=", new OperatorToken("<<=", TokenType.OPERATOR, 15, OperatorAssociativity.RIGHT, OperatorArity.BINARY, false)); // Левый сдвиг с присваиванием
        put(">>=", new OperatorToken(">>=", TokenType.OPERATOR, 15, OperatorAssociativity.RIGHT, OperatorArity.BINARY, false)); // Правый сдвиг с присваиванием
        put(">>>=", new OperatorToken(">>>=", TokenType.OPERATOR, 15, OperatorAssociativity.RIGHT, OperatorArity.BINARY, false)); // Беззнаковый правый сдвиг с присваиванием
        put("&=", new OperatorToken("&=", TokenType.OPERATOR, 15, OperatorAssociativity.RIGHT, OperatorArity.BINARY, false)); // Побитовое И с присваиванием
        put("^=", new OperatorToken("^=", TokenType.OPERATOR, 15, OperatorAssociativity.RIGHT, OperatorArity.BINARY, false)); // Побитовое исключающее ИЛИ с присваиванием
        put("|=", new OperatorToken("|=", TokenType.OPERATOR, 15, OperatorAssociativity.RIGHT, OperatorArity.BINARY, false)); // Побитовое ИЛИ с присваиванием
    }};


    public JavaTokenizer(String code, JavaLanguage javaLanguage) {
        super(code, javaLanguage);
    }

    @Override
    protected String[] getStopNodes() {
        return new String[] {"string_literal"};
    }

    @Override
    protected OperatorToken getOperatorByTokenName(String tokenName) {
        return operators.getOrDefault(tokenName, null);
    }

    @Override
    protected OperatorToken getOperator(String tokenValue, TSNode node) {
        if (node.getParent().getType().equals("unary_expression") && List.of("+", "-").contains(tokenValue)) {
            if (tokenValue.equals("+")) {
                return operators.get("UPLUS");
            } else if (tokenValue.equals("-")) {
                return operators.get("UMINUS");
            }
        }

        if (tokenValue.equals("(") && !node.getParent().isNull() && node.getParent().getType().equals("method_invocation")) {
            return operators.get("CALL_(");
        }

        if (tokenValue.equals(")") && !node.getParent().isNull() && node.getParent().getType().equals("method_invocation")) {
            return operators.get("CALL_)");
        }

        if (node.getParent().getType().equals("update_expression") &&
                TreeSitterUtils.getCodePiece(code, node.getParent()).startsWith("++") && tokenValue.equals("++")) {
            return operators.get("++U");
        }

        if (node.getParent().getType().equals("update_expression") &&
                TreeSitterUtils.getCodePiece(code, node.getParent()).startsWith("--")  && tokenValue.equals("--")) {
            return operators.get("--U");
        }

        return operators.getOrDefault(tokenValue, null);
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
            tokenType = TokenType.COMPOUND_OPEN_BRACE;
        } else if (type.equals("}")) {
            tokenType = TokenType.COMPOUND_CLOSE_BRACE;
        } else if (type.equals("(")) {
            tokenType = TokenType.OPEN_BRACE;
        } else if (type.equals(")")) {
            tokenType = TokenType.CLOSE_BRACE;
        } else if (type.equals(";")) {
            tokenType = TokenType.SEPARATOR;
        } else if (List.of("type_identifier", "identifier",
                "integral_type", "boolean_type").contains(type)) {
            if (!parent.isNull() && parent.getType().equals("method_invocation")) {
                tokenType = TokenType.CALLABLE_IDENTIFIER;
            } else {
                tokenType = TokenType.IDENTIFIER;
            }
        } else if (List.of("decimal_floating_point_literal", "decimal_integer_literal",
                "string_literal", "true", "false", "null_literal"
        ).contains(type)) {
            tokenType = TokenType.CONST;
        } else if (type.equals(",")) {
            tokenType = TokenType.COMMA;
        } else {
            tokenType = TokenType.UNKNOWN;
        }
        return new Token(TreeSitterUtils.getCodePiece(code, node), tokenType);
    }
}
