package org.vstu.meaningtree.nodes.literals;

import org.vstu.meaningtree.nodes.Expression;

public class UnmodifiableListLiteral extends PlainCollectionLiteral {
    public UnmodifiableListLiteral(Expression... content) {
        super(content);
    }
}
