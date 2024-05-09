package org.vstu.meaningtree.nodes;

import org.vstu.meaningtree.nodes.declarations.DeclarationArgument;
import org.vstu.meaningtree.nodes.definitions.DefinitionArgument;

import java.util.List;

public class ObjectNewExpression extends NewExpression {
    private final List<Expression> _constructorArguments;

    public ObjectNewExpression(Type type, Expression... constructorArguments) {
        super(type);
        _constructorArguments = List.of(constructorArguments);
    }

    public List<Expression> getConstructorArguments() {
        return _constructorArguments;
    }

    // anonymous classes unsupported

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
