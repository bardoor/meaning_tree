package org.vstu.meaningtree.utils.tokens;

public class Token {
    public final String value;
    public final TokenType type;

    private Object assignedValue = null;

    public Token(String value, TokenType type) {
        this.value = value;
        this.type = type;
    }

    @Override
    public String toString() {
        return String.format("token[value=\"%s\",type=%s]", value, type);
    }

    public void assignValue(Object tag) {
        assignedValue = tag;
    }

    public Object getAssignedValue() {
        return assignedValue;
    }
}

