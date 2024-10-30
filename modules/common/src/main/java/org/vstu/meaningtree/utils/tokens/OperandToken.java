package org.vstu.meaningtree.utils.tokens;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class OperandToken extends Token {
    protected OperatorToken operandOf;
    protected OperandPosition operandPos;

    public OperandToken(String value, TokenType type) {
        super(value, type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), operandOf, operandPos);
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

    public OperandToken clone() {
        OperandToken copy = new OperandToken(value, type);
        copy.assignValue(assignedValue);
        copy.setMetadata(operandOf, operandPos);
        return copy;
    }
}
