package org.vstu.meaningtree.nodes.literals;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Literal;

import java.util.ArrayList;
import java.util.List;

public class PlainCollectionLiteral extends Literal {
    private final List<Expression> _content;

    public PlainCollectionLiteral(Expression ... content) {
        _content = List.of(content);
    }

    public List<Expression> getList() {
        return new ArrayList<>(_content);
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
