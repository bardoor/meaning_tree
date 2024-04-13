package org.vstu.meaningtree.nodes.literals;

import org.vstu.meaningtree.nodes.Expression;

import java.util.List;

public class ListLiteral extends PlainCollectionLiteral {
    public ListLiteral(Expression ... content) {
        super(content);
    }
}
