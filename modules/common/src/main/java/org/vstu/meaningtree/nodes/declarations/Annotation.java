package org.vstu.meaningtree.nodes.declarations;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.identifiers.Identifier;

import java.util.List;

public class Annotation extends Declaration {
    private final List<Expression> arguments;
    private final Expression _function;

    public Annotation(Expression function, Expression... arguments) {
        _function = function;
        this.arguments = List.of(arguments);
    }

    public Expression[] getArguments() {
        return arguments.toArray(new Expression[0]);
    }

    public Expression getFunctionExpression() {
        return _function;
    }

    public boolean hasName() {
        return _function instanceof Identifier;
    }

    public Identifier getName() {
        return (Identifier) _function;
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
