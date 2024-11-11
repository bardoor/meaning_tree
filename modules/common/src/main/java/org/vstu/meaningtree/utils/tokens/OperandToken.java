package org.vstu.meaningtree.utils.tokens;

import java.util.Objects;

public class OperandToken extends Token {
    protected OperatorToken operandOf;

    @Override
    public boolean contentEquals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.contentEquals(o)) return false;
        OperandToken that = (OperandToken) o;
        return Objects.equals(operandOf, that.operandOf) && operandPos == that.operandPos;
    }

    /***
     * Сравнивает объекты только по базовому содержимому: типу и значению токена
     * @param o - другой токен
     * @return
     */
    public boolean baseEquals(OperandToken o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return super.contentEquals(o);
    }

    protected OperandPosition operandPos;

    public OperandToken(String value, TokenType type) {
        super(value, type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), operandOf, operandPos);
    }

    public void setMetadata(OperatorToken operator, OperandPosition pos) {
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
