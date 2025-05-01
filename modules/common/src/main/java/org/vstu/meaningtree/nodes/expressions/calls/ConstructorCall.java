package org.vstu.meaningtree.nodes.expressions.calls;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Type;
import org.vstu.meaningtree.nodes.interfaces.Callable;
import org.vstu.meaningtree.utils.TreeNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConstructorCall extends Expression implements Callable {
    @TreeNode protected List<Expression> arguments;
    @TreeNode protected Type constructorOwner;

    public ConstructorCall(Type constructorOwner, List<Expression> arguments) {
        this.arguments = arguments;
        this.constructorOwner = constructorOwner;
    }

    public ConstructorCall(Type constructorOwner, Expression ... arguments) {
        this(constructorOwner, List.of(arguments));
    }

    public List<Expression> getArguments() {
        return List.copyOf(arguments);
    }

    public Type getOwner() {
        return constructorOwner;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ConstructorCall that = (ConstructorCall) o;
        return Objects.equals(arguments, that.arguments) && Objects.equals(constructorOwner, that.constructorOwner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), arguments, constructorOwner);
    }

    @Override
    public ConstructorCall clone() {
        ConstructorCall obj = (ConstructorCall) super.clone();
        obj.arguments = new ArrayList<>(arguments.stream().map(Expression::clone).toList());
        obj.constructorOwner = constructorOwner.clone();
        return obj;
    }

    @Override
    public Expression getCallableName() {
        return getOwner();
    }
}
