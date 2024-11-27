package org.vstu.meaningtree.nodes.expressions.literals;

import org.vstu.meaningtree.nodes.Expression;

import java.util.List;

public class SetLiteral extends PlainCollectionLiteral {
    public SetLiteral(Expression... content) {
        super(content);
    }

    public SetLiteral(List<Expression> content) {
        super(content);
    }
}
