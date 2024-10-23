package org.vstu.meaningtree.utils.tokens;

import org.vstu.meaningtree.exceptions.MeaningTreeException;

public class TokenGroup {
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

    public TokenList toList() {
        return new TokenList(source.subList(start, stop));
    }

    public String toString() {
        return String.format("group%s", toList().stream().map((Token t) -> t.value).toList());
    }
}
