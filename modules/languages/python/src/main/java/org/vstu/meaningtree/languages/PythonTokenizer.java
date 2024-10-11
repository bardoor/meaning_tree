package org.vstu.meaningtree.languages;

import org.treesitter.TSNode;
import org.vstu.meaningtree.utils.OperatorAssociativity;
import org.vstu.meaningtree.utils.TokenType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PythonTokenizer extends LanguageTokenizer {

    private Map<String, Integer> prec = new HashMap<>() {{
        put("lambda", 1);                // Лямбда-выражения
        put("if", 2);                    // Условные выражения
        put("else", 2);                    // Условные выражения
        put("or", 3);                    // Логическое ИЛИ
        put("and", 4);                   // Логическое И
        put("not", 5);                   // Логическое НЕ
        put("in", 6);                    // Оператор in
        put("is", 6);                    // Оператор is
        put("<", 7);                     // Меньше
        put("<=", 7);                    // Меньше или равно
        put(">", 7);                     // Больше
        put(">=", 7);                    // Больше или равно
        put("==", 7);                    // Равно
        put("!=", 7);                    // Не равно
        put("|", 8);                     // Побитовое ИЛИ
        put("^", 9);                     // Побитовое исключающее ИЛИ
        put("&", 10);                    // Побитовое И
        put("<<", 11);                   // Левый сдвиг
        put(">>", 11);                   // Правый сдвиг
        put("+", 12);                    // Сложение
        put("-", 12);                    // Вычитание
        put("*", 13);                    // Умножение
        put("@", 13);                    // Матричное умножение
        put("/", 13);                    // Деление
        put("//", 13);                   // Целочисленное деление
        put("%", 13);                    // Остаток от деления
        put("**", 14);                   // Возведение в степень
        put("UPLUS", 15);                // Унарный плюс
        put("UMINUS", 15);               // Унарный минус
        put("~", 15);                    // Побитовая инверсия
        put("=", 16);                    // Присваивание
        put("+=", 16);                   // Сложение с присваиванием
        put("-=", 16);                   // Вычитание с присваиванием
        put("*=", 16);                   // Умножение с присваиванием
        put("/=", 16);                   // Деление с присваиванием
        put("//=", 16);                  // Целочисленное деление с присваиванием
        put("%=", 16);                   // Остаток от деления с присваиванием
        put("&=", 16);                   // Побитовое И с присваиванием
        put("|=", 16);                   // Побитовое ИЛИ с присваиванием
        put("^=", 16);                   // Побитовое исключающее ИЛИ с присваиванием
        put(">>=", 16);                  // Правый сдвиг с присваиванием
        put("<<=", 16);                  // Левый сдвиг с присваиванием
    }};

    private Map<String, OperatorAssociativity> assoc = new HashMap<>() {{
        put("lambda", OperatorAssociativity.RIGHT);          // Лямбда-выражения
        put("if", OperatorAssociativity.RIGHT);              // Условные выражения
        put("else", OperatorAssociativity.RIGHT);              // Условные выражения
        put("or", OperatorAssociativity.LEFT);               // Логическое ИЛИ
        put("and", OperatorAssociativity.LEFT);              // Логическое И
        put("not", OperatorAssociativity.RIGHT);             // Логическое НЕ
        put("in", OperatorAssociativity.LEFT);               // Оператор in
        put("is", OperatorAssociativity.LEFT);               // Оператор is
        put("<", OperatorAssociativity.LEFT);                // Меньше
        put("<=", OperatorAssociativity.LEFT);               // Меньше или равно
        put(">", OperatorAssociativity.LEFT);                // Больше
        put(">=", OperatorAssociativity.LEFT);               // Больше или равно
        put("==", OperatorAssociativity.LEFT);               // Равно
        put("!=", OperatorAssociativity.LEFT);               // Не равно
        put("|", OperatorAssociativity.LEFT);                // Побитовое ИЛИ
        put("^", OperatorAssociativity.LEFT);                // Побитовое исключающее ИЛИ
        put("&", OperatorAssociativity.LEFT);                // Побитовое И
        put("<<", OperatorAssociativity.LEFT);               // Левый сдвиг
        put(">>", OperatorAssociativity.LEFT);               // Правый сдвиг
        put("+", OperatorAssociativity.LEFT);                // Сложение
        put("-", OperatorAssociativity.LEFT);                // Вычитание
        put("*", OperatorAssociativity.LEFT);                // Умножение
        put("@", OperatorAssociativity.LEFT);                // Матричное умножение
        put("/", OperatorAssociativity.LEFT);                // Деление
        put("//", OperatorAssociativity.LEFT);               // Целочисленное деление
        put("%", OperatorAssociativity.LEFT);                // Остаток от деления
        put("**", OperatorAssociativity.RIGHT);              // Возведение в степень
        put("UPLUS", OperatorAssociativity.RIGHT);           // Унарный плюс
        put("UMINUS", OperatorAssociativity.RIGHT);          // Унарный минус
        put("~", OperatorAssociativity.RIGHT);               // Побитовая инверсия
        put("=", OperatorAssociativity.NON_ASSOC);           // Присваивание (неассоциативный)
        put("+=", OperatorAssociativity.NON_ASSOC);          // Сложение с присваиванием
        put("-=", OperatorAssociativity.NON_ASSOC);          // Вычитание с присваиванием
        put("*=", OperatorAssociativity.NON_ASSOC);          // Умножение с присваиванием
        put("/=", OperatorAssociativity.NON_ASSOC);          // Деление с присваиванием
        put("//=", OperatorAssociativity.NON_ASSOC);         // Целочисленное деление с присваиванием
        put("%=", OperatorAssociativity.NON_ASSOC);          // Остаток от деления с присваиванием
        put("&=", OperatorAssociativity.NON_ASSOC);          // Побитовое И с присваиванием
        put("|=", OperatorAssociativity.NON_ASSOC);          // Побитовое ИЛИ с присваиванием
        put("^=", OperatorAssociativity.NON_ASSOC);          // Побитовое исключающее ИЛИ с присваиванием
        put(">>=", OperatorAssociativity.NON_ASSOC);         // Правый сдвиг с присваиванием
        put("<<=", OperatorAssociativity.NON_ASSOC);         // Левый сдвиг с присваиванием
    }};


    public PythonTokenizer(String code, PythonLanguage parser) {
        super(code, parser);
    }

    @Override
    protected String[] getStopNodes() {
        return new String[] {"string"};
    }

    @Override
    protected int getOperatorPrecedence(String tokenValue, TSNode node) {
        if (node.getParent().getType().equals("unary_operator") && List.of("+", "-").contains(tokenValue)) {
            if (tokenValue.equals("+")) {
                return prec.get("UPLUS");
            } else if (tokenValue.equals("-")) {
                return prec.get("UMINUS");
            }
        }
        return prec.getOrDefault(tokenValue, -1);
    }

    @Override
    protected OperatorAssociativity getOperatorAssociativity(String tokenValue, TSNode node) {
        if (node.getParent().getType().equals("unary_operator") && List.of("+", "-").contains(tokenValue)) {
            if (tokenValue.equals("+")) {
                return assoc.get("UPLUS");
            } else if (tokenValue.equals("-")) {
                return assoc.get("UMINUS");
            }
        }

        return assoc.getOrDefault(tokenValue, null);
    }

    @Override
    protected TokenType recognizeTokenType(TSNode node) {
        String type = node.getType();
        if (List.of("{", "(", "[").contains(type)) {
            return TokenType.OPEN_BRACE;
        } else if (List.of("}", ")", "]").contains(type)) {
            return TokenType.CLOSE_BRACE;
        } else if (List.of(";", ":").contains(type)) {
            return TokenType.SEPARATOR;
        } else if (List.of("type_identifier", "identifier",
                "integral_type", "boolean_type").contains(type)) {
            return TokenType.IDENTIFIER;
        } else if (List.of(
                "+", "-", "*", "&", "/", "%", "^",
                ".", ":=", ">>", "|", "//",
                "<<", "not", "and", "or",
                "~", ">", "<", "==", "!=", ">=", "<=",
                "in", "is"
        ).contains(type)) {
            return TokenType.OPERATOR;
        } else if (List.of("integer", "float",
                "string", "true", "false", "none").contains(type)) {
            return TokenType.CONST;
        } else if (type.equals(",")) {
            return TokenType.COMMA;
        } else if (List.of("lambda", "if", "else").contains(type)) {
            return TokenType.KEYWORD;
        } else if (List.of("=", "+=", "-=", "*=", "/=",
                "//=", "%=", "^=", "&=", ">>=", "<<=", "|=").contains(type)) {
            return TokenType.OPERATOR;
        } else {
            return TokenType.UNKNOWN;
        }
    }
}
