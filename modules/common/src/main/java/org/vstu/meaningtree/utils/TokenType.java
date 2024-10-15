package org.vstu.meaningtree.utils;

public enum TokenType {
    OPERATOR,

    CONST,
    CALLABLE_IDENTIFIER,
    IDENTIFIER,
    KEYWORD,

    OPENING_BRACE,
    CLOSING_BRACE,
    SUBSCRIPT_OPENING_BRACE,
    SUBSCRIPT_CLOSING_BRACE,
    CALL_OPENING_BRACE,
    CALL_CLOSING_BRACE,
    COMPOUND_OPENING_BRACE,
    COMPOUND_CLOSING_BRACE,
    INITIALIZER_LIST_OPENING_BRACE,
    INITIALIZER_LIST_CLOSING_BRACE,

    STATEMENT_TOKEN,
    SEPARATOR,
    COMMA,
    UNKNOWN
}
