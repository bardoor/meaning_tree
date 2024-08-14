package org.vstu.meaningtree.nodes.io;

import org.vstu.meaningtree.nodes.Expression;

import java.util.List;

public class FormatPrint extends PrintValueSequence {
    private final Expression _formatString;

    public FormatPrint(Expression formatString, List<Expression> expressions) {
        super(expressions);
        _formatString = formatString;
    }

    public Expression getFormatString() {
        return _formatString;
    }
}
