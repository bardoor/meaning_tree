package org.vstu.meaningtree.nodes.expressions.literals;

import org.jetbrains.annotations.Nullable;
import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Type;
import org.vstu.meaningtree.utils.TreeNode;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.SequencedMap;

public class DictionaryLiteral extends CollectionLiteral {
    @TreeNode(type = TreeNode.Type.MAP_NODE_KEY_VALUE) private SequencedMap<Expression, Expression> content;
    @TreeNode @Nullable private Type keyTypeHint;
    @TreeNode @Nullable private Type valueTypeHint;

    public DictionaryLiteral(SequencedMap<Expression, Expression> content) {
        this.content = new LinkedHashMap<>(content);
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
        return new LinkedHashMap<>(content);
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
        return Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), content);
    }

    @Override
    public DictionaryLiteral clone() {
        DictionaryLiteral obj = (DictionaryLiteral) super.clone();
        obj.content = new LinkedHashMap<>(content);
        if (keyTypeHint != null) obj.keyTypeHint = keyTypeHint.clone();
        if (valueTypeHint != null) obj.valueTypeHint = valueTypeHint.clone();
        return obj;
    }

    public List<UnmodifiableListLiteral> asPairsListLiteral() {
        return getDictionary().entrySet().stream().map(entry ->
                new UnmodifiableListLiteral(entry.getKey(), entry.getValue())).toList();
    }
}
