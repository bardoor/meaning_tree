package org.vstu.meaningtree.nodes.statements;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Node;

import java.util.Optional;

public class GeneralForLoop extends ForLoop {
    //TODO: initializer is expression? What about VariableDeclaration
    private final Optional<Node> _initializer;
    private final Optional<Node> _condition;
    private final Optional<Node> _update;
    private final CompoundStatement _body;

    public GeneralForLoop(Node initializer, Node condition, Node update, CompoundStatement body) {
        this._initializer = Optional.ofNullable(initializer);
        this._condition = Optional.ofNullable(condition);
        this._update = Optional.ofNullable(update);
        this._body = body;
    }

    public boolean hasInitializer() {
        return _initializer.isPresent();
    }

    public Node getInitializer() {
        if (!hasInitializer()) {
            throw new RuntimeException("No initizalier");
        }

        return _initializer.get();
    }

    public boolean hasCondition() {
        return _condition.isPresent();
    }

    public Node getCondition() {
        if (!hasCondition()) {
            throw new RuntimeException("No condition");
        }

        return _condition.get();
    }

    public boolean hasUpdate() {
        return _update.isPresent();
    }

    public Node getUpdate() {
        if (!hasUpdate()) {
            throw new RuntimeException("No update");
        }

        return _update.get();
    }

    public Node getBody() {
        return _body;
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
