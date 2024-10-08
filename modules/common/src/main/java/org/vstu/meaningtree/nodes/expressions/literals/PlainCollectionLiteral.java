package org.vstu.meaningtree.nodes.expressions.literals;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.expressions.Literal;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class PlainCollectionLiteral extends Literal {
    private final List<Expression> _content;

    public PlainCollectionLiteral(Expression ... content) {
        _content = List.of(content);
    }

    public PlainCollectionLiteral(List<Expression> exprs) {_content = new ArrayList<>(exprs);}

    public List<Expression> getList() {
        return List.copyOf(_content);
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlainCollectionLiteral that = (PlainCollectionLiteral) o;
        return Objects.equals(_content, that._content);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(_content);
    }
}
