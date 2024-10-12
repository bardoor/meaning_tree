package org.vstu.meaningtree.nodes.expressions.literals;

import org.vstu.meaningtree.nodes.Expression;

import java.util.List;

public class ArrayLiteral extends PlainCollectionLiteral {
    public ArrayLiteral(Expression ... content) {
        super(content);
    }

    public ArrayLiteral(List<Expression> expressionList) {
        super(expressionList);
    }

    public ListLiteral toListLiteral() {
        return new ListLiteral(getList());
    }
}