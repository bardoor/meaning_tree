package org.vstu.meaningtree.nodes.literals;

import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

import org.vstu.meaningtree.nodes.Expression;

public class DictionaryLiteral extends Literal {
    private final SortedMap<Expression, Expression> _content;

    public DictionaryLiteral(SortedMap<Expression, Expression> content) {
        this._content = new TreeMap<>(content);
    }

    public SortedMap<Expression, Expression> getDictionary() {
        return new TreeMap<>(_content);
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
        return Objects.hashCode(_content);
    }
}
