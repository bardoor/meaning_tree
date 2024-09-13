package org.vstu.meaningtree.nodes.io;

import org.jetbrains.annotations.NotNull;
import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.expressions.calls.FunctionCall;

import java.util.List;

public class InputCommand extends FunctionCall {

    public InputCommand(@NotNull List<Expression> values) {
        super(null, values);
    }
}
