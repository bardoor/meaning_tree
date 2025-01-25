package org.vstu.meaningtree.nodes.io;

import org.vstu.meaningtree.nodes.Expression;

import java.util.List;
import java.util.Objects;

public class FormatInput extends InputCommand {
    private final Expression _formatString;

    public FormatInput(Expression formatString, Expression... values) {
        super(List.of(values));
        _formatString = formatString;
    }

    public Expression getFormatString() {
        return _formatString;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        FormatInput that = (FormatInput) o;
        return Objects.equals(_formatString, that._formatString);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), _formatString);
    }
}

