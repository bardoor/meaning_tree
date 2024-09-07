package org.vstu.meaningtree.nodes.io;

import org.jetbrains.annotations.NotNull;
import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.FunctionCall;

import java.util.List;

public abstract class PrintCommand extends FunctionCall {

    public PrintCommand(@NotNull List<Expression> values) {
        super(null, values);
    }

    @Override
    public boolean hasFunctionName() {
        // Язык определяет это имя самостоятельно
        return true;
    }
}
