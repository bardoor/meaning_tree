package org.vstu.meaningtree.nodes.expressions.other;

import org.vstu.meaningtree.nodes.Expression;

import java.util.Objects;

public class SizeofExpression extends Expression {
    Expression internalValue;

    public SizeofExpression(Expression expr) {
        this.internalValue = expr;
    }

    public Expression getExpression() {
        return internalValue;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SizeofExpression that = (SizeofExpression) o;
        return Objects.equals(internalValue, that.internalValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), internalValue);
    }
}
