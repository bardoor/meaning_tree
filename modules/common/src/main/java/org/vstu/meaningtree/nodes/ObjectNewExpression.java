package org.vstu.meaningtree.nodes;

import org.vstu.meaningtree.nodes.definitions.Argument;

import java.util.List;

public class ObjectNewExpression extends NewExpression {
    private final List<Argument> _constructorArguments;

    public ObjectNewExpression(Type type, Argument ... constructorArguments) {
        super(type);
        _constructorArguments = List.of(constructorArguments);
    }

    public List<Argument> getConstructorArguments() {
        return _constructorArguments;
    }

    // anonymous classes unsupported

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
