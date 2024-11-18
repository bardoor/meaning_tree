package org.vstu.meaningtree.nodes.expressions.literals;

import org.jetbrains.annotations.Nullable;
import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Type;
import org.vstu.meaningtree.nodes.expressions.Literal;

import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.SequencedMap;

public class DictionaryLiteral extends Literal {
    private final SequencedMap<Expression, Expression> _content;
    private @Nullable Type keyTypeHint;
    private @Nullable Type valueTypeHint;

    public DictionaryLiteral(SequencedMap<Expression, Expression> content) {
        this._content = new LinkedHashMap<>(content);
    }

    public void setKeyTypeHint(@Nullable Type type) {
        this.keyTypeHint = type;
    }

    @Nullable
    public Type getKeyTypeHint() {
        return keyTypeHint;
    }

    public void setValueTypeHint(@Nullable Type type) {
        this.valueTypeHint = type;
    }

    @Nullable
    public Type getValueTypeHint() {
        return valueTypeHint;
    }

    public SequencedMap<Expression, Expression> getDictionary() {
        return new LinkedHashMap<>(_content);
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DictionaryLiteral that = (DictionaryLiteral) o;
        return Objects.equals(_content, that._content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), _content);
    }
}
