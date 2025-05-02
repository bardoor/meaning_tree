package org.vstu.meaningtree.nodes.expressions.calls;

import org.vstu.meaningtree.iterators.utils.TreeNode;
import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.expressions.identifiers.SimpleIdentifier;
import org.vstu.meaningtree.nodes.expressions.other.MemberAccess;

import java.util.List;
import java.util.Objects;

public class MethodCall extends FunctionCall {
    @TreeNode private Expression object;

    public MethodCall(Expression object, SimpleIdentifier methodName, Expression... arguments) {
        super(methodName, arguments);
        this.object = object;
    }

    public MethodCall(Expression object, SimpleIdentifier methodName, List<Expression> arguments) {
        super(methodName, arguments);
        this.object = object;
    }

    @Override
    public Expression getCallableName() {
        return new MemberAccess(object, getFunctionName());
    }

    public Expression getObject() {
        return object;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        MethodCall that = (MethodCall) o;
        return Objects.equals(object, that.object);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), object);
    }

    @Override
    public MethodCall clone() {
        MethodCall obj = (MethodCall) super.clone();
        obj.object = object.clone();
        return obj;
    }
}
