package org.vstu.meaningtree.nodes.memory;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.expressions.calls.FunctionCall;
import org.vstu.meaningtree.nodes.expressions.other.DeleteExpression;

public class MemoryFreeCall extends FunctionCall {
    public MemoryFreeCall(Expression argument) {
        super(null, argument);
    }

    public DeleteExpression toDelete() {
        return new DeleteExpression(_arguments.getFirst());
    }
}
