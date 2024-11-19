package org.vstu.meaningtree.nodes.io;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.expressions.literals.StringLiteral;

import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        FormatPrint that = (FormatPrint) o;
        return Objects.equals(_formatString, that._formatString);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), _formatString);
    }
}
