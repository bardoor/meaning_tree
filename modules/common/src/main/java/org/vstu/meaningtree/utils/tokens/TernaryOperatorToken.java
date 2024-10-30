package org.vstu.meaningtree.utils.tokens;

import java.util.Objects;

public class TernaryOperatorToken extends OperatorToken {
    public final int operatorTokenPosition;

    TernaryOperatorToken(String token, int tokenPlace, int precedence, OperatorAssociativity assoc, boolean isStrictOrder) {
        super(token, TokenType.OPERATOR, precedence, assoc, OperatorArity.TERNARY, isStrictOrder, OperatorTokenPosition.INFIX);
        this.operatorTokenPosition = tokenPlace;
    }


    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), operatorTokenPosition);
    }

    public TernaryOperatorToken clone() {
        TernaryOperatorToken copy = new TernaryOperatorToken(value, operatorTokenPosition, precedence, assoc, isStrictOrder);
        copy.assignValue(assignedValue);
        copy.setMetadata(operandOf, operandPos);
        return copy;
    }
}
