package org.vstu.meaningtree.nodes.statements;

import org.vstu.meaningtree.nodes.Expression;

import java.util.Optional;

public class GeneralForLoop extends ForLoop {
    //TODO: initializer is expression? What about VariableDeclaration
    private final Optional<Expression> initializer;
    private final Optional<Expression> condition;
    private final Optional<Expression> update;
    private final CompoundStatement body;

    public GeneralForLoop(Expression initializer, Expression condition, Expression update, CompoundStatement body) {
        this.initializer = Optional.ofNullable(initializer);
        this.condition = Optional.ofNullable(condition);
        this.update = Optional.ofNullable(update);
        this.body = body;
    }


    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
