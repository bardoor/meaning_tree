package org.vstu.meaningtree.nodes.expressions.literals;

import org.vstu.meaningtree.nodes.Expression;

public class ListLiteral extends PlainCollectionLiteral {
    public ListLiteral(Expression ... content) {
        super(content);
    }
}
