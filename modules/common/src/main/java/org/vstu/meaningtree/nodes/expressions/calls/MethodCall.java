package org.vstu.meaningtree.nodes.expressions.calls;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.expressions.Identifier;

import java.util.List;

public class MethodCall extends FunctionCall {
    private final Expression _object;

    public MethodCall(Expression object, Identifier methodName, Expression... arguments) {
        super(methodName, arguments);
        _object = object;
    }

    public MethodCall(Expression object, Identifier methodName, List<Expression> arguments) {
        super(methodName, arguments);
        _object = object;
    }

    public Expression getObject() {
        return _object;
    }
}
