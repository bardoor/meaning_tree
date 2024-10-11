package org.vstu.meaningtree.languages;

import org.treesitter.TSNode;
import org.vstu.meaningtree.utils.OperatorAssociativity;
import org.vstu.meaningtree.utils.TokenType;
import org.vstu.meaningtree.utils.TreeSitterUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CppTokenizer extends LanguageTokenizer {
    private Map<String, Integer> prec = new HashMap<>() {{
        put("::", 1);                // Оператор разрешения области видимости
        put("->", 2);                // Указатель на член структуры
        put(".", 2);                 // Доступ к члену структуры
        put("++", 2);                // Инкремент
        put("--", 2);                // Декремент
        put("++U", 3);                // Инкремент
        put("--U", 3);                // Декремент
        put("UMINUS", 3);            // Унарный минус
        put("UPLUS", 3);             // Унарный плюс
        put("POINTER_&", 3);         // Унарный минус
        put("POINTER_*", 3);         // Унарный плюс
        put("~", 3);                 // Побитовая инверсия
        put("!", 3);                 // Логическое НЕ
        put("sizeof", 3);            // Оператор sizeof
        put("new", 3);               // Оператор new
        put("delete", 3);            // Оператор delete

        put("*", 4);                 // Умножение
        put("/", 4);                 // Деление
        put("%", 4);                 // Остаток от деления

        put("+", 5);                 // Сложение
        put("-", 5);                 // Вычитание

        put("<<", 6);                // Левый сдвиг
        put(">>", 6);                // Правый сдвиг

        put("<", 7);                 // Меньше
        put("<=", 7);                // Меньше или равно
        put(">", 7);                 // Больше
        put(">=", 7);                // Больше или равно

        put("==", 8);                // Равно
        put("!=", 8);                // Не равно

        put("&", 9);                 // Побитовое И

        put("^", 10);                // Побитовое исключающее ИЛИ

        put("|", 11);                // Побитовое ИЛИ

        put("&&", 12);               // Логическое И

        put("||", 13);               // Логическое ИЛИ

        put("?", 14);                // Тернарный оператор
        put(":", 14);                // Тернарный оператор

        put("=", 15);                // Присваивание
        put("+=", 15);               // Сложение с присваиванием
        put("-=", 15);               // Вычитание с присваиванием
        put("*=", 15);               // Умножение с присваиванием
        put("/=", 15);               // Деление с присваиванием
        put("%=", 15);               // Остаток с присваиванием
        put("<<=", 15);              // Левый сдвиг с присваиванием
        put(">>=", 15);              // Правый сдвиг с присваиванием
        put("&=", 15);               // Побитовое И с присваиванием
        put("^=", 15);               // Побитовое исключающее ИЛИ с присваиванием
        put("|=", 15);               // Побитовое ИЛИ с присваиванием

        put(",", 16);                // Запятая
    }};

    private Map<String, OperatorAssociativity> assoc = new HashMap<>() {{
        put("::", OperatorAssociativity.LEFT);            // Оператор разрешения области видимости
        put("->", OperatorAssociativity.LEFT);            // Указатель на член структуры
        put(".", OperatorAssociativity.LEFT);             // Доступ к члену структуры
        put("++", OperatorAssociativity.LEFT);       // Инкремент (унарный)
        put("--", OperatorAssociativity.LEFT);       // Декремент (унарный)
        put("++U", OperatorAssociativity.RIGHT);       // Инкремент (унарный) префиксный
        put("--U", OperatorAssociativity.RIGHT);       // Декремент (унарный) префиксный
        put("~", OperatorAssociativity.RIGHT);            // Побитовая инверсия (унарный)
        put("!", OperatorAssociativity.RIGHT);            // Логическое НЕ (унарный)
        put("sizeof", OperatorAssociativity.RIGHT);   // Оператор sizeof
        put("new", OperatorAssociativity.RIGHT);      // Оператор new
        put("delete", OperatorAssociativity.RIGHT);   // Оператор delete
        put("UPLUS", OperatorAssociativity.RIGHT);        // Унарный плюс
        put("UMINUS", OperatorAssociativity.RIGHT);       // Унарный минус
        put("POINTER_&", OperatorAssociativity.RIGHT);    // Унарное получение адреса
        put("POINTER_*", OperatorAssociativity.RIGHT);    // Унарное разыменование

        put("*", OperatorAssociativity.LEFT);             // Умножение (бинарный)
        put("/", OperatorAssociativity.LEFT);             // Деление (бинарный)
        put("%", OperatorAssociativity.LEFT);             // Остаток от деления (бинарный)

        put("+", OperatorAssociativity.LEFT);             // Сложение (бинарный)
        put("-", OperatorAssociativity.LEFT);             // Вычитание (бинарный)

        put("<<", OperatorAssociativity.LEFT);            // Левый сдвиг
        put(">>", OperatorAssociativity.LEFT);            // Правый сдвиг

        put("<", OperatorAssociativity.LEFT);             // Меньше
        put("<=", OperatorAssociativity.LEFT);            // Меньше или равно
        put(">", OperatorAssociativity.LEFT);             // Больше
        put(">=", OperatorAssociativity.LEFT);            // Больше или равно

        put("==", OperatorAssociativity.LEFT);            // Равно
        put("!=", OperatorAssociativity.LEFT);            // Не равно

        put("&", OperatorAssociativity.LEFT);             // Побитовое И (бинарный)

        put("^", OperatorAssociativity.LEFT);             // Побитовое исключающее ИЛИ (бинарный)

        put("|", OperatorAssociativity.LEFT);             // Побитовое ИЛИ (бинарный)

        put("&&", OperatorAssociativity.LEFT);            // Логическое И

        put("||", OperatorAssociativity.LEFT);            // Логическое ИЛИ

        put("?", OperatorAssociativity.RIGHT);            // Тернарный оператор
        put(":", OperatorAssociativity.RIGHT);            // Тернарный оператор

        put("=", OperatorAssociativity.RIGHT);            // Присваивание
        put("+=", OperatorAssociativity.RIGHT);           // Сложение с присваиванием
        put("-=", OperatorAssociativity.RIGHT);           // Вычитание с присваиванием
        put("*=", OperatorAssociativity.RIGHT);           // Умножение с присваиванием
        put("/=", OperatorAssociativity.RIGHT);           // Деление с присваиванием
        put("%=", OperatorAssociativity.RIGHT);           // Остаток с присваиванием
        put("<<=", OperatorAssociativity.RIGHT);          // Левый сдвиг с присваиванием
        put(">>=", OperatorAssociativity.RIGHT);          // Правый сдвиг с присваиванием
        put("&=", OperatorAssociativity.RIGHT);           // Побитовое И с присваиванием
        put("^=", OperatorAssociativity.RIGHT);           // Побитовое исключающее ИЛИ с присваиванием
        put("|=", OperatorAssociativity.RIGHT);           // Побитовое ИЛИ с присваиванием
    }};

    public CppTokenizer(String code, CppLanguage parser) {
        super(code, parser);
    }

    @Override
    protected String[] getStopNodes() {
        return new String[] {"user_defined_literal"};
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

        if (node.getParent().getType().equals("pointer_expression") && List.of("*", "&").contains(tokenValue)) {
            if (tokenValue.equals("*")) {
                return prec.get("POINTER_*");
            } else if (tokenValue.equals("&")) {
                return prec.get("POINTER_&");
            }
        }

        return prec.getOrDefault(tokenValue, -1);
    }

    @Override
    protected OperatorAssociativity getOperatorAssociativity(String tokenValue, TSNode node) {
        if (node.getParent().getType().equals("unary_expression") && List.of("+", "-").contains(tokenValue)) {
            if (tokenValue.equals("+")) {
                return assoc.get("UPLUS");
            } else if (tokenValue.equals("-")) {
                return assoc.get("UMINUS");
            }
        }

        if (node.getParent().getType().equals("update_expression") &&
                TreeSitterUtils.getCodePiece(code, node.getParent()).startsWith("++")) {
            return assoc.get("++U");
        }

        if (node.getParent().getType().equals("update_expression") &&
                TreeSitterUtils.getCodePiece(code, node.getParent()).startsWith("--")) {
            return assoc.get("--U");
        }

        if (node.getParent().getType().equals("pointer_expression") && List.of("*", "&").contains(tokenValue)) {
            if (tokenValue.equals("*")) {
                return assoc.get("POINTER_*");
            } else if (tokenValue.equals("&")) {
                return assoc.get("POINTER_&");
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
        } else if (List.of(";").contains(type)) {
            return TokenType.SEPARATOR;
        } else if (List.of("identifier", "namespace_identifier", "type_identifier", "field_identifier").contains(type)) {
            return TokenType.IDENTIFIER;
        } else if (List.of(
                "+", "-", "*", "&", "/", "%", "^", "|",
                "++", "--", "->", ".", "=", "+=", "&=", "-=", "/=",
                "%=", "^=", "*=", "&&=", "||=", "|=", ">>", "||", "&&",
                "<<", ">>=", "<<=", "?", ":", "!", "~", ">", "<", "==", "!=",
                ">=", "<=", "<=>", "sizeof", "new", "delete"
        ).contains(type)) {
            return TokenType.OPERATOR;
        } else if (List.of("number_literal", "string_literal", "true", "false", "null", "user_defined_literal").contains(type)) {
            return TokenType.CONST;
        } else if (type.equals(",")) {
            return TokenType.COMMA;
        } else {
            return TokenType.UNKNOWN;
        }
    }
}
