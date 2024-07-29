package org.vstu.meaningtree.nodes.literals;

import java.util.*;

import org.vstu.meaningtree.nodes.Expression;

public class DictionaryLiteral extends Literal {
    private final SequencedMap<Expression, Expression> _content;

    public DictionaryLiteral(SequencedMap<Expression, Expression> content) {
        this._content = new LinkedHashMap<>(content);
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
        return Objects.hashCode(_content);
    }
}
