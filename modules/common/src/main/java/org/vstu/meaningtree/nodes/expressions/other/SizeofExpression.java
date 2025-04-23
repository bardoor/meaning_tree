package org.vstu.meaningtree.nodes.expressions.other;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.expressions.ParenthesizedExpression;
import org.vstu.meaningtree.nodes.expressions.calls.FunctionCall;
import org.vstu.meaningtree.nodes.expressions.identifiers.SimpleIdentifier;

import java.util.Objects;

public class SizeofExpression extends Expression {
    Expression internalValue;

    public SizeofExpression(Expression expr) {
        if (expr instanceof ParenthesizedExpression paren) {
            expr = paren.getExpression();
        }
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

    @Override
    public SizeofExpression clone() {
        SizeofExpression obj = (SizeofExpression) super.clone();
        obj.internalValue = internalValue.clone();
        return obj;
    }

    public FunctionCall toCall() {
        return new FunctionCall(new SimpleIdentifier("sizeof"), internalValue);
    }
}
