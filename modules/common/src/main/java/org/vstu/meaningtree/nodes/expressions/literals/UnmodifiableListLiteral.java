package org.vstu.meaningtree.nodes.expressions.literals;

import org.vstu.meaningtree.nodes.Expression;

public class UnmodifiableListLiteral extends PlainCollectionLiteral {
    public UnmodifiableListLiteral(Expression... content) {
        super(content);
    }
}
