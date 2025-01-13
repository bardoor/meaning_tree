package org.vstu.meaningtree.nodes.expressions.literals;

import org.vstu.meaningtree.nodes.Expression;

import java.util.List;

public class UnmodifiableListLiteral extends PlainCollectionLiteral {
    public UnmodifiableListLiteral(Expression... content) {
        super(content);
    }

    public UnmodifiableListLiteral(List<Expression> content) {
        super(content);
    }
}
