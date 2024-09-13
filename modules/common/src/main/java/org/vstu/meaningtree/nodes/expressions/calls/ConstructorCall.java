package org.vstu.meaningtree.nodes.expressions.calls;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Type;

import java.util.List;

public class ConstructorCall extends Expression {
    protected final List<Expression> _arguments;
    protected final Type constructorOwner;

    public ConstructorCall(Type constructorOwner, List<Expression> arguments) {
        _arguments = arguments;
        this.constructorOwner = constructorOwner;
    }

    public ConstructorCall(Type constructorOwner, Expression ... arguments) {
        this(constructorOwner, List.of(arguments));
    }

    public List<Expression> getArguments() {
        return _arguments;
    }

    public Type getOwner() {
        return constructorOwner;
    }
}
