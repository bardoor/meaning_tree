package org.vstu.meaningtree.nodes.expressions.literals;

import org.vstu.meaningtree.nodes.Expression;

import java.util.List;

public class ListLiteral extends PlainCollectionLiteral {
    public ListLiteral(Expression ... content) {
        super(content);
    }

    public ListLiteral(List<Expression> expressionList) {
        super(expressionList);
    }

    public ArrayLiteral toArrayLiteral() {
        return new ArrayLiteral(getList());
    }
}
