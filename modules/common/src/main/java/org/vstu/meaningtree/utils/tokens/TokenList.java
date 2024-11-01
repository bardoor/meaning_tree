package org.vstu.meaningtree.utils.tokens;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TokenList extends ArrayList<Token> {
    public TokenList() {
        super();
    }

    public TokenList(List<Token> tokens) {
        super(tokens);
    }

    public TokenList subtract(TokenGroup group) {
        TokenList copy = clone();
        copy.subList(group.start, group.stop).clear();
        return copy;
    }

    public int findLeftmostToken(String value) {
        return stream().map((Token t) -> t.value).toList().indexOf(value);
    }

    public int findRightmostToken(String value) {
        int reversedResult = reversed().stream().map((Token t) -> t.value).toList().indexOf(value);
        return reversedResult >= size() ? -1 : size() - reversedResult;
    }

    public void setMetadata(OperatorToken token, OperandPosition pos) {
        for (int i = 0; i < size(); i++) {
            Token t = get(i);
            if (!(t instanceof OperandToken)) {
                set(i, new OperandToken(t.value, t.type));
            }
            OperandToken op = ((OperandToken)get(i));
            if (op.operandOf() == null) {
                op.setMetadata(token, pos);
            }
        }
    }

    public void assignValue(Object tag) {
        for (Token t : this) {
            if (t.getAssignedValue() == null)
                t.assignValue(tag);
        }
    }

    public int findComplex(int tokenIndex, int complexPos) {
        assert get(tokenIndex) instanceof ComplexOperatorToken;
        ComplexOperatorToken complex = (ComplexOperatorToken) get(tokenIndex);
        int nesting = 0;
        int start = complex.positionOfToken > complexPos ? complexPos : size() - 1;
        int stop = complex.positionOfToken > complexPos ? size() - 1 : complexPos;
        int step = complex.positionOfToken > complexPos ? 1 : -1;
        for (int i = start; i < stop; i += step){
            if (get(i) instanceof ComplexOperatorToken currentComplex) {
                boolean increaseCondition = currentComplex.positionOfToken == complexPos;
                boolean decreaseCondition = currentComplex.positionOfToken != complexPos;

                if (currentComplex.isEqualComplex(complex) &&
                        increaseCondition
                ) {
                    nesting += 1;
                } else if (currentComplex.isEqualComplex(complex) && decreaseCondition) {
                    nesting -= 1;
                    if (nesting <= 0) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }

    public int findClosingComplex(int tokenIndex) {
        assert get(tokenIndex) instanceof ComplexOperatorToken;
        ComplexOperatorToken complex = (ComplexOperatorToken) get(tokenIndex);
        return findComplex(tokenIndex, complex.getClosingPosition());
    }

    public int findOpeningComplex(int tokenIndex) {
        return findComplex(tokenIndex, 0);
    }


    public TokenList clone() {
        return (TokenList) super.clone();
    }

    public Map<OperandPosition, TokenGroup> findOperands(int opIndexToken) {
        OperandToken op = (OperandToken) get(opIndexToken);
        Map<OperandPosition, TokenGroup> result = new HashMap<>();
        int i = 0;
        int start = -1;
        int stop = -1;
        OperandPosition oldPos = null;
        while (i < size()) {
            if (get(i) instanceof OperandToken operand && operand.operandOf() != null && operand.operandOf().equals(op)) {
                OperandPosition pos = operand.operandPosition();
                if (oldPos != null && !oldPos.equals(pos)) {
                    start = i;
                }
                if (start == -1) {
                    start = i;
                }
                stop = i + 1;
                while (stop < size() && ((OperandToken)get(stop)).operandOf() != null && ((OperandToken)get(stop)).operandOf().equals(op)
                        && ((OperandToken)get(stop)).operandPosition().equals(pos)) {
                    stop = i + 1;
                    i++;
                }
                result.put(pos, new TokenGroup(start, stop, this));
                oldPos = pos;
            }
            i++;
        }
        return result;
    }
}
