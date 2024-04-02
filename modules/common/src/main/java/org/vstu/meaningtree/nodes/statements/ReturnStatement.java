package org.vstu.meaningtree.nodes.statements;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Statement;

import java.util.Optional;

public class ReturnStatement extends Statement {
    private final Optional<Expression> _expr;

    public ReturnStatement(Expression expr) {
        _expr = Optional.ofNullable(expr);
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }

    public Expression getExpression() {
        if (!hasExpression()) {
            throw new RuntimeException("Empty return");
        }
        return _expr.get();
    }

    public boolean hasExpression() {
        return _expr.isPresent();
    }
}
