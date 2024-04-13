package org.vstu.meaningtree.nodes.literals;

import org.vstu.meaningtree.nodes.Expression;

public class SetLiteral extends PlainCollectionLiteral {
    public SetLiteral(Expression... content) {
        super(content);
    }
}
