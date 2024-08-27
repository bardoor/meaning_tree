package org.vstu.meaningtree.nodes.io;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.FunctionCall;
import org.vstu.meaningtree.nodes.Statement;

import java.util.List;

public abstract class PrintStatement extends FunctionCall {

    public PrintStatement(List<Expression> values) {
        super(null, values);
    }
}
