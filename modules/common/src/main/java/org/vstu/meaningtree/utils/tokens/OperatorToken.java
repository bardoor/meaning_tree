package org.vstu.meaningtree.utils.tokens;

public class OperatorToken extends Token {
    public final int precedence;
    public final OperatorAssociativity assoc;
    public final OperatorArity arity;
    public final boolean isStrictOrder;

    public OperatorToken(String value,
                         TokenType type,
                         int precedence,
                         OperatorAssociativity assoc,
                         OperatorArity arity,
                         boolean isStrictOrder
    ) {
        super(value, type);
        this.precedence = precedence;
        this.assoc = assoc;
        this.arity = arity;
        this.isStrictOrder = isStrictOrder;
    }

    @Override
    public String toString() {
        return String.format("token[value=\"%s\",type=%s,prec=%s,assoc=%s,arity=%s,strictOrder=%s]",
                value, type, precedence, assoc, arity, isStrictOrder);
    }
}
