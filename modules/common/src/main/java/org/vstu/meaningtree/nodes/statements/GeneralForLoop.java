package org.vstu.meaningtree.nodes.statements;

import org.vstu.meaningtree.nodes.HasInitialization;
import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Statement;

import java.util.Optional;

public class GeneralForLoop extends ForLoop {
    private final Optional<HasInitialization> _initializer;
    private final Optional<Expression> _condition;
    private final Optional<Expression> _update;
    private Statement _body;

    public GeneralForLoop(HasInitialization initializer, Expression condition, Expression update, Statement body) {
        this._initializer = Optional.ofNullable(initializer);
        this._condition = Optional.ofNullable(condition);
        this._update = Optional.ofNullable(update);
        this._body = body;
    }

    @Override
    public void makeBodyCompound() {
        if (!(_body instanceof CompoundStatement)) {
            _body = new CompoundStatement(_body);
        }
    }

    public boolean hasInitializer() {
        return _initializer.isPresent();
    }

    public HasInitialization getInitializer() {
        if (!hasInitializer()) {
            throw new RuntimeException("No initizalier");
        }

        return _initializer.get();
    }

    public boolean hasCondition() {
        return _condition.isPresent();
    }

    public Expression getCondition() {
        if (!hasCondition()) {
            throw new RuntimeException("No condition");
        }

        return _condition.get();
    }

    public boolean hasUpdate() {
        return _update.isPresent();
    }

    public Expression getUpdate() {
        if (!hasUpdate()) {
            throw new RuntimeException("No update");
        }

        return _update.get();
    }

    public Statement getBody() {
        return _body;
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
