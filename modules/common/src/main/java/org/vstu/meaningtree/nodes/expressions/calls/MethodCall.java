package org.vstu.meaningtree.nodes.expressions.calls;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.expressions.Identifier;

import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        MethodCall that = (MethodCall) o;
        return Objects.equals(_object, that._object);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), _object);
    }
}
