package org.vstu.meaningtree.nodes.statements;

import org.vstu.meaningtree.nodes.Expression;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ExpressionSequence extends Expression  {
    // Применяется в случаях Python в `return a, b`, простых перечислениях выражений, множественных индексов x[a, b]
    private List<Expression> _expressions;

    public ExpressionSequence(Expression ... expressions) {
        this(List.of(expressions));
    }

    public ExpressionSequence(List<Expression> expressions) {
        _expressions = List.copyOf(expressions);
    }

    public List<Expression> getExpressions() {
        return _expressions;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ExpressionSequence that = (ExpressionSequence) o;
        return Objects.equals(_expressions, that._expressions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), _expressions);
    }

    @Override
    public ExpressionSequence clone() {
        ExpressionSequence obj = (ExpressionSequence) super.clone();
        obj._expressions = new ArrayList<>(_expressions.stream().map(Expression::clone).toList());
        return obj;
    }
}
