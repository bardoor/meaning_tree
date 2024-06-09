package org.vstu.meaningtree.nodes;

import org.vstu.meaningtree.nodes.identifiers.SimpleIdentifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FunctionCall that = (FunctionCall) o;
        return Objects.equals(functionName, that.functionName) && Objects.equals(arguments, that.arguments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(functionName, arguments);
    }
}
