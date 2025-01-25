package org.vstu.meaningtree.nodes.expressions.other;

import org.vstu.meaningtree.nodes.Expression;

import java.util.List;

public class CommaExpression extends ExpressionSequence{
    public CommaExpression(Expression... expressions) {
        super(expressions);
    }

    public CommaExpression(List<Expression> expressions) {
        super(expressions);
    }
}
