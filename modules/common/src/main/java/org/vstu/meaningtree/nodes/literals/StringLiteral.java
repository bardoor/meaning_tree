package org.vstu.meaningtree.nodes.literals;

import org.apache.commons.text.StringEscapeUtils;

import java.util.Objects;

public class StringLiteral extends Literal {
    public enum Type {
        NONE,
        RAW, // строка, которая не воспринимает символы экранирования,
        // т.е. например \n представляется в памяти как \\n, а не как символ новой строки
    }

    protected final String value;
    protected final Type stringType;

    // Строка хранится в чистом виде, не экранированная. Т.е. представление как в памяти требуемого языка программирования
    // Интерполяция не поддерживается в этом классе

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
