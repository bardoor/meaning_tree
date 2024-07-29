package org.vstu.meaningtree.nodes.statements;

import org.vstu.meaningtree.nodes.Expression;

import java.util.ArrayList;
import java.util.List;

public class ExpressionSequence extends Expression  {
    // Применяется в случаях Python в `return a, b`, простых перечислениях выражений, множественных индексов x[a, b]
    private List<Expression> _expressions;

    public ExpressionSequence(Expression ... expressions) {
        _expressions = List.of(expressions);
    }

    public ExpressionSequence(List<Expression> expressions) {
        _expressions = new ArrayList<>(expressions);
    }

    public List<Expression> getExpressions() {
        return _expressions;
    }
}
