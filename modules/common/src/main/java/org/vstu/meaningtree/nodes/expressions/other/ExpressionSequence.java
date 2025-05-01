package org.vstu.meaningtree.nodes.expressions.other;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.utils.TreeNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Применяется в случаях Python в `return a, b`, простых перечислениях выражений, множественных индексов x[a, b]
 * Запятая в C++ - подкласс этого класса
 * **/
public class ExpressionSequence extends Expression  {
    @TreeNode private List<Expression> expressions;

    public ExpressionSequence(Expression ... expressions) {
        this(List.of(expressions));
    }

    public ExpressionSequence(List<Expression> expressions) {
        this.expressions = List.copyOf(expressions);
    }

    public List<Expression> getExpressions() {
        return expressions;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ExpressionSequence that = (ExpressionSequence) o;
        return Objects.equals(expressions, that.expressions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), expressions);
    }

    @Override
    public ExpressionSequence clone() {
        ExpressionSequence obj = (ExpressionSequence) super.clone();
        obj.expressions = new ArrayList<>(expressions.stream().map(Expression::clone).toList());
        return obj;
    }
}
