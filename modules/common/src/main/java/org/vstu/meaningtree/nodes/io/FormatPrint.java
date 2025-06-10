package org.vstu.meaningtree.nodes.io;

import org.vstu.meaningtree.iterators.utils.TreeNode;
import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.expressions.literals.StringLiteral;

import java.util.List;
import java.util.Objects;

public class FormatPrint extends PrintValues {
    @TreeNode private Expression formatString;

    public FormatPrint(Expression formatString, List<Expression> values) {
        super(
                values,
                StringLiteral.fromUnescaped("", StringLiteral.Type.NONE),
                StringLiteral.fromUnescaped("", StringLiteral.Type.NONE)
        );
        this.formatString = formatString;
    }

    public FormatPrint(Expression formatString, Expression ... values) {
        this(formatString, List.of(values));
    }

    public Expression getFormatString() {
        return formatString;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        FormatPrint that = (FormatPrint) o;
        return Objects.equals(formatString, that.formatString);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), formatString);
    }
}
