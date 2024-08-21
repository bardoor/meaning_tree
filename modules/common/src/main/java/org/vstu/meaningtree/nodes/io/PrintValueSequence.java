package org.vstu.meaningtree.nodes.io;

import org.vstu.meaningtree.nodes.Expression;

import java.util.List;

public class PrintValueSequence extends PrintStatement {
    private final List<Expression> _expressions;

    public PrintValueSequence(List<Expression> expressions) {
        _expressions = List.copyOf(expressions);
    }

    public List<Expression> getExpressions() {
        return _expressions;
    }
}
