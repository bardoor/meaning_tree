package org.vstu.meaningtree.nodes.io;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.expressions.literals.StringLiteral;

import java.util.List;

public abstract class FormatPrint extends PrintValues {
    private final Expression _formatString;

    protected FormatPrint(Expression formatString) {
        super(
                List.of(),
                StringLiteral.fromUnescaped("", StringLiteral.Type.NONE),
                StringLiteral.fromUnescaped("", StringLiteral.Type.NONE)
        );
        _formatString = formatString;
    }


    public Expression getFormatString() {
        return _formatString;
    }
}
