package org.vstu.meaningtree.nodes;

import org.vstu.meaningtree.nodes.identifiers.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FunctionCall extends Expression {
    protected final Identifier _functionName;

    public Identifier getFunctionName() {
        return _functionName;
    }

    public List<Expression> getArguments() {
        return List.copyOf(_arguments);
    }

    protected final List<Expression> _arguments;

    public FunctionCall(Identifier functionName, Expression ... arguments) {
        this(functionName, List.of(arguments));
    }

    public FunctionCall(Identifier functionName, List<Expression> arguments) {
        this._functionName = functionName;
        this._arguments = arguments;
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
        return Objects.equals(_functionName, that._functionName) && Objects.equals(_arguments, that._arguments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_functionName, _arguments);
    }
}
