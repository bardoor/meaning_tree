package org.vstu.meaningtree.utils.tokens;

import java.util.List;

public class ComplexOperatorToken extends OperatorToken {
    public final int positionOfToken;
    protected final List<String> complexTokenValues;

    ComplexOperatorToken(int positionOfToken, String value,
                         TokenType type,
                         OperatorTokenPosition tokenPos,
                         int precedence,
                         OperatorAssociativity assoc,
                         OperatorArity arity,
                         boolean isStrictOrder, String[] complexTokenValues) {
        super(value, type, precedence, assoc, arity, isStrictOrder, tokenPos);
        assert complexTokenValues.length >= 2;
        this.positionOfToken = positionOfToken;
        this.complexTokenValues = List.of(complexTokenValues);
    }

    public boolean isOpening() {
        return positionOfToken == 0;
    }

    public boolean isClosing() {
        return positionOfToken == complexTokenValues.size() - 1;
    }

    public boolean isEqualComplex(ComplexOperatorToken that) {
        return that.complexTokenValues.equals(this.complexTokenValues) && precedence == that.precedence
                && isStrictOrder == that.isStrictOrder && assoc == that.assoc
                && arity == that.arity && tokenPos == that.tokenPos;
    }

    public int getClosingPosition() {
        return complexTokenValues.size() - 1;
    }
}
