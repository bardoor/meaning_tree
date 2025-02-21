package org.vstu.meaningtree.utils.tokens;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
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

    public List<Pair<OperatorToken, OperandPosition>> operandOfHierarchy() {
        List<Pair<OperatorToken, OperandPosition>> ops = new ArrayList<>();
        OperandToken tmp = this;
        while (tmp.operandOf() != null) {
            ops.add(ImmutablePair.of(tmp.operandOf(), tmp.operandPosition()));
            tmp = tmp.operandOf();
        }
        return ops.reversed();
    }

    public boolean isInOperandOf(OperatorToken opTok) {
        return operandOfHierarchy().stream().map(Pair::getLeft).toList().contains(opTok);
    }

    public boolean isInOperandOf(OperatorToken opTok, OperandPosition pos) {
        return operandOfHierarchy().stream()
                .anyMatch(pair -> pair.getLeft().equals(opTok) && pair.getRight().equals(pos));
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

    public OperandToken clone(String newName) {
        OperandToken copy = new OperandToken(newName, type);
        copy.assignValue(assignedValue);
        copy.setMetadata(operandOf, operandPos);
        return copy;
    }
}
