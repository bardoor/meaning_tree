package org.vstu.meaningtree.languages;

import org.treesitter.TSNode;
import org.vstu.meaningtree.utils.OperatorAssociativity;
import org.vstu.meaningtree.utils.TokenType;
import org.vstu.meaningtree.utils.TreeSitterUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JavaTokenizer extends LanguageTokenizer {
    private Map<String, Integer> prec = new HashMap<>() {{
        put("++", 2);                    // Постфиксный инкремент
        put("--", 2);                    // Постфиксный декремент

        put("+U", 3);                    // Унарный плюс
        put("-U", 3);                    // Унарный минус
        put("++U", 3);                   // Префиксный инкремент
        put("--U", 3);                   // Префиксный декремент
        put("!", 3);                     // Логическое НЕ
        put("~", 3);                     // Побитовая инверсия

        put("*", 4);                     // Умножение
        put("/", 4);                     // Деление
        put("%", 4);                     // Остаток от деления

        put("+", 5);                     // Сложение (бинарный)
        put("-", 5);                     // Вычитание (бинарный)

        put("<<", 6);                    // Левый сдвиг
        put(">>", 6);                    // Правый сдвиг с заполнением знака
        put(">>>", 6);                   // Правый сдвиг без заполнения знака

        put("<", 7);                     // Меньше
        put("<=", 7);                    // Меньше или равно
        put(">", 7);                     // Больше
        put(">=", 7);                    // Больше или равно
        put("instanceof", 7);            // Проверка на тип

        put("==", 8);                    // Равно
        put("!=", 8);                    // Не равно

        put("&", 9);                     // Побитовое И

        put("^", 10);                    // Побитовое исключающее ИЛИ

        put("|", 11);                    // Побитовое ИЛИ

        put("&&", 12);                   // Логическое И

        put("||", 13);                   // Логическое ИЛИ

        put("?", 14);                    // Тернарный оператор

        put("=", 15);                    // Присваивание
        put("+=", 15);                   // Сложение с присваиванием
        put("-=", 15);                   // Вычитание с присваиванием
        put("*=", 15);                   // Умножение с присваиванием
        put("/=", 15);                   // Деление с присваиванием
        put("%=", 15);                   // Остаток с присваиванием
        put("<<=", 15);                  // Левый сдвиг с присваиванием
        put(">>=", 15);                  // Правый сдвиг с присваиванием
        put(">>>=", 15);                 // Беззнаковый правый сдвиг с присваиванием
        put("&=", 15);                   // Побитовое И с присваиванием
        put("^=", 15);                   // Побитовое исключающее ИЛИ с присваиванием
        put("|=", 15);                   // Побитовое ИЛИ с присваиванием
    }};

    private Map<String, OperatorAssociativity> assoc = new HashMap<>() {{
        put("++", OperatorAssociativity.LEFT);       // Постфиксный инкремент
        put("--", OperatorAssociativity.LEFT);       // Постфиксный декремент

        put("+U", OperatorAssociativity.RIGHT);           // Унарный плюс
        put("-U", OperatorAssociativity.RIGHT);           // Унарный минус
        put("++U", OperatorAssociativity.RIGHT);          // Префиксный инкремент
        put("--U", OperatorAssociativity.RIGHT);          // Префиксный декремент
        put("!", OperatorAssociativity.RIGHT);            // Логическое НЕ
        put("~", OperatorAssociativity.RIGHT);            // Побитовая инверсия

        put("*", OperatorAssociativity.LEFT);             // Умножение
        put("/", OperatorAssociativity.LEFT);             // Деление
        put("%", OperatorAssociativity.LEFT);             // Остаток от деления

        put("+", OperatorAssociativity.LEFT);             // Сложение (бинарный)
        put("-", OperatorAssociativity.LEFT);             // Вычитание (бинарный)

        put("<<", OperatorAssociativity.LEFT);            // Левый сдвиг
        put(">>", OperatorAssociativity.LEFT);            // Правый сдвиг
        put(">>>", OperatorAssociativity.LEFT);           // Беззнаковый правый сдвиг

        put("<", OperatorAssociativity.LEFT);             // Меньше
        put("<=", OperatorAssociativity.LEFT);            // Меньше или равно
        put(">", OperatorAssociativity.LEFT);             // Больше
        put(">=", OperatorAssociativity.LEFT);            // Больше или равно
        put("instanceof", OperatorAssociativity.LEFT);    // Проверка на тип

        put("==", OperatorAssociativity.LEFT);            // Равно
        put("!=", OperatorAssociativity.LEFT);            // Не равно

        put("&", OperatorAssociativity.LEFT);             // Побитовое И

        put("^", OperatorAssociativity.LEFT);             // Побитовое исключающее ИЛИ

        put("|", OperatorAssociativity.LEFT);             // Побитовое ИЛИ

        put("&&", OperatorAssociativity.LEFT);            // Логическое И

        put("||", OperatorAssociativity.LEFT);            // Логическое ИЛИ

        put("?", OperatorAssociativity.RIGHT);            // Тернарный оператор

        put("=", OperatorAssociativity.RIGHT);            // Присваивание
        put("+=", OperatorAssociativity.RIGHT);           // Сложение с присваиванием
        put("-=", OperatorAssociativity.RIGHT);           // Вычитание с присваиванием
        put("*=", OperatorAssociativity.RIGHT);           // Умножение с присваиванием
        put("/=", OperatorAssociativity.RIGHT);           // Деление с присваиванием
        put("%=", OperatorAssociativity.RIGHT);           // Остаток с присваиванием
        put("<<=", OperatorAssociativity.RIGHT);          // Левый сдвиг с присваиванием
        put(">>=", OperatorAssociativity.RIGHT);          // Правый сдвиг с присваиванием
        put(">>>=", OperatorAssociativity.RIGHT);         // Беззнаковый правый сдвиг с присваиванием
        put("&=", OperatorAssociativity.RIGHT);           // Побитовое И с присваиванием
        put("^=", OperatorAssociativity.RIGHT);           // Побитовое исключающее ИЛИ с присваиванием
        put("|=", OperatorAssociativity.RIGHT);           // Побитовое ИЛИ с присваиванием
    }};


    public JavaTokenizer(String code, JavaLanguage javaLanguage) {
        super(code, javaLanguage);
    }

    @Override
    protected String[] getStopNodes() {
        return new String[] {"string_literal"};
    }

    @Override
    protected int getOperatorPrecedence(String tokenValue, TSNode node) {
        if (node.getParent().getType().equals("unary_expression") && List.of("+", "-").contains(tokenValue)) {
            if (tokenValue.equals("+")) {
                return prec.get("UPLUS");
            } else if (tokenValue.equals("-")) {
                return prec.get("UMINUS");
            }
        }

        if (node.getParent().getType().equals("update_expression") &&
                TreeSitterUtils.getCodePiece(code, node.getParent()).startsWith("++")) {
            return prec.get("++U");
        }

        if (node.getParent().getType().equals("update_expression") &&
                TreeSitterUtils.getCodePiece(code, node.getParent()).startsWith("--")) {
            return prec.get("--U");
        }

        return prec.getOrDefault(tokenValue, -1);
    }

    @Override
    protected OperatorAssociativity getOperatorAssociativity(String tokenValue, TSNode node) {
        return null;
    }

    @Override
    protected TokenType recognizeTokenType(TSNode node) {
        String type = node.getType();
        if (List.of("{", "(", "[").contains(type)) {
            return TokenType.OPEN_BRACE;
        } else if (List.of("}", ")", "]").contains(type)) {
            return TokenType.CLOSE_BRACE;
        } else if (List.of(";").contains(type)) {
            return TokenType.SEPARATOR;
        } else if (List.of("type_identifier", "identifier",
                "integral_type", "boolean_type").contains(type)) {
            return TokenType.IDENTIFIER;
        } else if (List.of(
                "+", "-", "*", "&", "/", "%", "^", "|",
                "++", "--", ".", "=", "+=", "&=", "-=", "/=",
                "%=", "^=", "*=", "&&=", "||=", "|=", ">>", "||", "&&",
                "<<", ">>=", "<<=", "?", ":", "!", "~", ">", "<", "==", "!=",
                ">=", "<="
        ).contains(type)) {
            return TokenType.OPERATOR;
        } else if (List.of("decimal_floating_point_literal", "decimal_integer_literal",
                "string_literal", "true", "false", "null_literal"
        ).contains(type)) {
            return TokenType.CONST;
        } else if (type.equals(",")) {
            return TokenType.COMMA;
        } else {
            return TokenType.UNKNOWN;
        }
    }
}
