package org.vstu.meaningtree.utils.tokens;

public class TernaryOperatorToken extends OperatorToken {
    public final int operatorTokenPosition;

    TernaryOperatorToken(String token, int tokenPlace, int precedence, OperatorAssociativity assoc, boolean isStrictOrder) {
        super(token, TokenType.OPERATOR, precedence, assoc, OperatorArity.TERNARY, isStrictOrder, OperatorTokenPosition.INFIX);
        this.operatorTokenPosition = tokenPlace;
    }
}
