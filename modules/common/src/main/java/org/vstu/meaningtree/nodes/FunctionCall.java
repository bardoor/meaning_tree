package org.vstu.meaningtree.nodes;

import org.vstu.meaningtree.nodes.identifiers.SimpleIdentifier;

import java.util.ArrayList;
import java.util.List;

public class FunctionCall extends Expression {
    protected final SimpleIdentifier functionName;

    public SimpleIdentifier getFunctionName() {
        return functionName;
    }

    public List<Expression> getArguments() {
        return new ArrayList<>(arguments);
    }

    protected final ArrayList<Expression> arguments;

    public FunctionCall(SimpleIdentifier functionName, ArrayList<Expression> arguments) {
        this.functionName = functionName;
        this.arguments = arguments;
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
