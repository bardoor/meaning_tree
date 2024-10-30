package org.vstu.meaningtree.utils.tokens;

import java.util.Objects;

public class Token {
    public final String value;
    public final TokenType type;

    protected static int idCounter = 0;

    protected Object assignedValue = null;
    private int id = ++idCounter;

    public Token(String value, TokenType type) {
        this.value = value;
        this.type = type;
    }

    @Override
    public String toString() {
        return String.format("token[\"%s\",%s%s]", value, type, assignedValue == null ? "" : ",tag=".concat(assignedValue.toString()));
    }

    public boolean contentEquals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Token token = (Token) o;
        return Objects.equals(value, token.value) && type == token.type && Objects.equals(assignedValue, token.assignedValue);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Token token = (Token) o;
        return id == token.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, type, assignedValue, id);
    }

    public void assignValue(Object tag) {
        assignedValue = tag;
    }

    public Object getAssignedValue() {
        return assignedValue;
    }

    public int getId() {
        return id;
    }

    public Token clone() {
        Token copy = new Token(value, type);
        copy.assignValue(assignedValue);
        return copy;
    }
}

