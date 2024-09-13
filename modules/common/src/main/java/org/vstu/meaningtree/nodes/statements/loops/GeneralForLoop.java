package org.vstu.meaningtree.nodes.statements.loops;

import org.jetbrains.annotations.Nullable;
import org.vstu.meaningtree.nodes.interfaces.HasInitialization;
import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Statement;
import org.vstu.meaningtree.nodes.statements.CompoundStatement;

public class GeneralForLoop extends ForLoop {
    @Nullable
    private final HasInitialization _initializer;

    @Nullable
    private final Expression _condition;

    @Nullable
    private final Expression _update;
    private Statement _body;

    public GeneralForLoop(@Nullable HasInitialization initializer, @Nullable Expression condition,
                          @Nullable Expression update, Statement body) {
        this._initializer = initializer;
        this._condition = condition;
        this._update = update;
        this._body = body;
    }

    @Override
    public void makeBodyCompound() {
        if (!(_body instanceof CompoundStatement)) {
            _body = new CompoundStatement(_body);
        }
    }

    public boolean hasInitializer() {
        return _initializer != null;
    }

    public HasInitialization getInitializer() {
        if (!hasInitializer()) {
            throw new RuntimeException("No initializer");
        }

        return _initializer;
    }

    public boolean hasCondition() {
        return _condition != null;
    }

    public Expression getCondition() {
        if (!hasCondition()) {
            throw new RuntimeException("No condition");
        }

        return _condition;
    }

    public boolean hasUpdate() {
        return _update != null;
    }

    public Expression getUpdate() {
        if (!hasUpdate()) {
            throw new RuntimeException("No update");
        }

        return _update;
    }

    public Statement getBody() {
        return _body;
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
