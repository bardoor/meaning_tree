package org.vstu.meaningtree.nodes.expressions.newexpr;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Type;

import java.util.List;

public class PlacementNewExpression extends ObjectNewExpression{
    public PlacementNewExpression(Type type, Expression... constructorArguments) {
        super(type, constructorArguments);
    }

    public PlacementNewExpression(Type type, List<Expression> exprs) {
        super(type, exprs);
    }
}
