package org.vstu.meaningtree.nodes.literals;

import org.apache.commons.text.StringEscapeUtils;

import java.util.Objects;

public class StringLiteral extends Literal {
    public enum Type {
        NONE,
        INTERPOLATION, //строка, в которую можно подставить значения
        RAW, // строка, которая игнорирует символы экранирования
    }

    protected final String value;
    protected final Type stringType;

    // NOTE Строка хранится в чистом виде, не экранированная. Т.е. представление как в памяти

    public String getUnescapedValue() {
        return value;
    }

    public String getEscapedValue() {
        return StringEscapeUtils.escapeJava(value);
    }

    private StringLiteral(String value, Type stringType) {
        this.value = value;
        this.stringType = stringType;
    }

    public static StringLiteral fromUnescaped(String value, Type stringType) {
        return new StringLiteral(value, stringType);
    }

    public static StringLiteral fromEscaped(String value, Type stringType) {
        value = StringEscapeUtils.unescapeJava(value);
        return new StringLiteral(value, stringType);
    }

    public boolean isMultiline() {
        return getUnescapedValue().contains("\n");
    }

    public Type getStringType() {
        return stringType;
    }

    @Override
    public String generateDot() {
        return String.format("%s [label=\"%s\"];\n", _id, value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StringLiteral that = (StringLiteral) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
