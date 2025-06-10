package org.vstu.meaningtree.nodes.declarations;

import org.vstu.meaningtree.iterators.utils.TreeNode;
import org.vstu.meaningtree.nodes.Declaration;
import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.expressions.Identifier;

import java.util.List;

public class Annotation extends Declaration {
    @TreeNode private List<Expression> arguments;
    @TreeNode private final Expression function;

    public Annotation(Expression function, Expression... arguments) {
        this.function = function;
        this.arguments = List.of(arguments);
    }

    public Expression[] getArguments() {
        return arguments.toArray(new Expression[0]);
    }

    public Expression getFunctionExpression() {
        return function;
    }

    public boolean hasName() {
        return function instanceof Identifier;
    }

    public Identifier getName() {
        return (Identifier) function;
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
