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
    
    public TokenList clone() {
        return (TokenList) super.clone();
    }

    public Map<OperandPosition, TokenGroup> findOperands(int opIndexToken) {
        OperandToken op = (OperandToken) get(opIndexToken);
        Map<OperandPosition, TokenGroup> result = new HashMap<>();
        int i = 0;
        while (i < size()) {
            if (get(i) instanceof OperandToken operand && operand.operandOf().equals(op)) {
                int start = i;
                int stop = i + 1;
                OperandPosition pos = operand.operandPosition();
                while (stop < size() && ((OperandToken)get(stop)).operandOf().equals(op)
                        && ((OperandToken)get(stop)).operandPosition().equals(pos)) {
                    stop = i + 1;
                    i++;
                }
                result.put(pos, new TokenGroup(start, stop, this));
            }
            i++;
        }
        return result;
    }
}
