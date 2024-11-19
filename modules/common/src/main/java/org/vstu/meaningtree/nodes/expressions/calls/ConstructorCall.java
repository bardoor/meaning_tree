package org.vstu.meaningtree.nodes.expressions.calls;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConstructorCall extends Expression {
    protected List<Expression> _arguments;
    protected Type constructorOwner;

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ConstructorCall that = (ConstructorCall) o;
        return Objects.equals(_arguments, that._arguments) && Objects.equals(constructorOwner, that.constructorOwner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), _arguments, constructorOwner);
    }

    @Override
    public ConstructorCall clone() {
        ConstructorCall obj = (ConstructorCall) super.clone();
        obj._arguments = new ArrayList<>(_arguments.stream().map(Expression::clone).toList());
        obj.constructorOwner = constructorOwner.clone();
        return obj;
    }
}
