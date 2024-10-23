package org.vstu.meaningtree.utils.tokens;

import java.util.ArrayList;
import java.util.List;

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
    
    public TokenList clone() {
        return (TokenList) super.clone();
    }
}
