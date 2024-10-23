package org.vstu.meaningtree.utils.tokens;

import org.jetbrains.annotations.NotNull;

public class OperandToken extends Token {
    private OperatorToken operandOf;
    private OperandPosition operandPos;

    public OperandToken(String value, TokenType type) {
        super(value, type);
    }

    public void setMetadata(@NotNull OperatorToken operator, @NotNull OperandPosition pos) {
        this.operandOf = operator;
        this.operandPos = pos;
    }

    public OperatorToken operandOf() {
        return operandOf;
    }

    public OperandPosition operandPosition() {
        return operandPos;
    }
}
