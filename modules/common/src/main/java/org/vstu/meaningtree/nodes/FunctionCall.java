package org.vstu.meaningtree.nodes;

import org.vstu.meaningtree.nodes.identifiers.SimpleIdentifier;

import java.util.ArrayList;
import java.util.List;

public class FunctionCall extends Expression {
    protected final Identifier functionName;

    public Identifier getFunctionName() {
        return functionName;
    }

    public List<Expression> getArguments() {
        return new ArrayList<>(arguments);
    }

    protected final List<Expression> arguments;

    public FunctionCall(Identifier functionName, Expression ... arguments) {
        this(functionName, List.of(arguments));
    }

    public FunctionCall(Identifier functionName, List<Expression> arguments) {
        this.functionName = functionName;
        this.arguments = arguments;
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
