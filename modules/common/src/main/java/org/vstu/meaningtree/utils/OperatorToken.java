package org.vstu.meaningtree.utils;

public class OperatorToken extends Token {
    public final int precedence;
    public final OperatorAssociativity assoc;

    public OperatorToken(String value, TokenType type, int precedence, OperatorAssociativity assoc) {
        super(value, type);
        this.precedence = precedence;
        this.assoc = assoc;
    }

    @Override
    public String toString() {
        return String.format("token[value=\"%s\",type=%s,prec=%s,assoc=%s]", value, type, precedence, assoc);
    }
}
