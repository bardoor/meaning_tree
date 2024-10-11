package org.vstu.meaningtree.utils;

public class OperatorToken extends Token {
    public final int precedence;
    public final OperatorAssociativity assoc;

    public OperatorToken(String value, int precedence, OperatorAssociativity assoc) {
        super(value, TokenType.OPERATOR);
        this.precedence = precedence;
        this.assoc = assoc;
    }
}
