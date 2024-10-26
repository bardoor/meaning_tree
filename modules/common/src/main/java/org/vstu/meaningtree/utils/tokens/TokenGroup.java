package org.vstu.meaningtree.utils.tokens;

import org.jetbrains.annotations.NotNull;
import org.vstu.meaningtree.exceptions.MeaningTreeException;

import java.util.Iterator;

public class TokenGroup implements Iterable<Token> {
    public final int start;
    public final int stop;
    public final TokenList source;

    public TokenGroup(int start, int stop, TokenList source) {
        this.start = start;
        this.stop = stop;
        this.source = source;
        if (start < 0 || stop > source.size()) {
            throw new MeaningTreeException("Invalid indexes in token group");
        }
    }

    public TokenList copyToList() {
        return new TokenList(source.subList(start, stop));
    }

    @NotNull
    public Iterator<Token> iterator() {
        return source.subList(start, stop).iterator();
    }

    public int length() {
        return stop - start;
    }

    public void setMetadata(OperatorToken token, OperandPosition pos) {
        for (int i = start; i < stop; i++) {
            Token t = source.get(i);
            if (!(t instanceof OperandToken)) {
                source.set(i, new OperandToken(t.value, t.type));
                source.get(i).assignValue(t.getAssignedValue());
            }
            OperandToken op = ((OperandToken)source.get(i));
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

    public String toString() {
        return String.format("group%s", copyToList().stream().map((Token t) -> "\"" + t.value + "\"").toList());
    }
}
