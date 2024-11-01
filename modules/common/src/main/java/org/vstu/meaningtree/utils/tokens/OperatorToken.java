package org.vstu.meaningtree.utils.tokens;

import org.vstu.meaningtree.exceptions.MeaningTreeException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class OperatorToken extends OperandToken {
    public final int precedence;
    public final OperatorAssociativity assoc;
    public final OperatorArity arity;
    public final boolean isStrictOrder;
    public final OperatorTokenPosition tokenPos;

    public final OperatorType additionalOpType;

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), precedence, assoc, arity, isStrictOrder, tokenPos);
    }

    public OperatorToken(String value,
                         TokenType type,
                         int precedence,
                         OperatorAssociativity assoc,
                         OperatorArity arity,
                         boolean isStrictOrder,
                         OperatorTokenPosition tokenPos,
                         OperatorType additionalOpType
    ) {
        super(value, type);
        this.precedence = precedence;
        this.assoc = assoc;
        this.arity = arity;
        this.tokenPos = tokenPos;
        this.isStrictOrder = isStrictOrder;
        this.additionalOpType = additionalOpType;
    }

    public OperatorToken(String value,
                         TokenType type,
                         int precedence,
                         OperatorAssociativity assoc,
                         OperatorArity arity,
                         boolean isStrictOrder,
                         OperatorTokenPosition tokenPos
    ) {
        this(value, type, precedence, assoc, arity, isStrictOrder, tokenPos, OperatorType.OTHER);
    }


    @Override
    public boolean contentEquals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.contentEquals(o)) return false;
        OperatorToken that = (OperatorToken) o;
        return precedence == that.precedence && isStrictOrder == that.isStrictOrder && assoc == that.assoc && arity == that.arity && tokenPos == that.tokenPos;
    }

    public OperatorToken(String value,
                         TokenType type,
                         int precedence,
                         OperatorAssociativity assoc,
                         OperatorArity arity,
                         boolean isStrictOrder
    ) {
        this(value, type, precedence, assoc, arity, isStrictOrder,
                arity == OperatorArity.UNARY ? OperatorTokenPosition.PREFIX : OperatorTokenPosition.INFIX, OperatorType.OTHER);
    }

    public static List<OperatorToken> makeComplex(int precedence, OperatorArity arity, OperatorAssociativity assoc,
                                            boolean isStrictOrder, String[] tokens, TokenType[] types, OperatorTokenPosition[] pos) {
        assert types.length == tokens.length;
        if (tokens.length != 2) {
            throw new MeaningTreeException("Malformed ternary operator");
        }
        return new ArrayList<>() {{
            for (int i = 0; i < tokens.length; i++) {
                add(new ComplexOperatorToken(i, tokens[i], types[i], pos[i], precedence, assoc, arity, isStrictOrder, tokens));
            }
        }};
    }

    public static List<OperatorToken> makeComplex(int precedence, OperatorArity arity, OperatorAssociativity assoc,
                                                  boolean isStrictOrder, String[] tokens, TokenType[] types) {

        OperatorTokenPosition[] pos = new OperatorTokenPosition[tokens.length];
        Arrays.fill(pos, OperatorTokenPosition.INFIX);
        return makeComplex(precedence, arity, assoc, isStrictOrder, tokens, types, pos);
    }


        @Override
    public String toString() {
        return String.format("token[\"%s\",%s%s,prec=%s,assoc=%s,arity=%s,strictOrder=%s]",
                value, type, getAssignedValue() == null ? "" : ",tag=".concat(getAssignedValue().toString()), precedence, assoc, arity, isStrictOrder);
    }

    public OperatorToken clone() {
        OperatorToken copy = new OperatorToken(value, type, precedence, assoc, arity, isStrictOrder, tokenPos);
        copy.assignValue(assignedValue);
        copy.setMetadata(operandOf, operandPos);
        return copy;
    }
}
