package org.vstu.meaningtree.nodes.literals;

import java.util.SortedMap;
import java.util.TreeMap;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Literal;

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
}
