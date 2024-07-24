package org.vstu.meaningtree.nodes.statements;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Statement;

import java.util.Optional;

public class ReturnStatement extends Statement {
    private final Expression _expr;

    public ReturnStatement(Expression expr) {
        _expr = expr;
    }

    public ReturnStatement() {
        this(null);
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }

    public Optional<Expression> getExpression() {
        return Optional.ofNullable(_expr);
    }
}
