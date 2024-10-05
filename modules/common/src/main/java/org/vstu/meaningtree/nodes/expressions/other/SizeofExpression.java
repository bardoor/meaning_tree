package org.vstu.meaningtree.nodes.expressions.other;

import org.vstu.meaningtree.nodes.Expression;

public class SizeofExpression extends Expression {
    Expression internalValue;

    public SizeofExpression(Expression expr) {
        this.internalValue = expr;
    }

    public Expression getExpression() {
        return internalValue;
    }
}
