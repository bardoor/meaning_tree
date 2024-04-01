package org.vstu.meaningtree.nodes.logical;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.UnaryExpression;

public class NotOp extends UnaryExpression {
    public NotOp(Expression argument) {
        super(argument);
    }
}
