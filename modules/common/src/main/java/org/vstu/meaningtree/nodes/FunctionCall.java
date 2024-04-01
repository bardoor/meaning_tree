package org.vstu.meaningtree.nodes;

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

    protected final ArrayList<Expression> arguments;

    public FunctionCall(Identifier functionName, ArrayList<Expression> arguments) {
        this.functionName = functionName;
        this.arguments = arguments;
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
