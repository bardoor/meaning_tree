package org.vstu.meaningtree.nodes.io;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.literals.StringLiteral;

import java.util.List;
import java.util.Objects;

public class PrintValues extends PrintCommand {
    @Nullable
    public final StringLiteral separator;

    @Nullable
    public final StringLiteral end;

    public PrintValues(
            @NotNull List<Expression> values,
            @Nullable StringLiteral separator,
            @Nullable StringLiteral end
    ) {
        super(values);
        this.separator = separator;
        this.end = end;
    }

    public boolean addsNewLine() {
        return end != null && end.getUnescapedValue().equals("\n");
    }

    public int valuesCount() {
        return _arguments.size();
    }

    public boolean hasAnyValues() {
        return valuesCount() > 0;
    }

    public static class PrintValuesBuilder {
        @Nullable
        private StringLiteral _separator = null;

        @Nullable
        private StringLiteral _end = null;

        @Nullable
        private List<Expression> _values;

        public PrintValuesBuilder separateBy(StringLiteral separator) {
            _separator = separator;
            return this;
        }

        public PrintValuesBuilder separateBy(String separator) {
            return separateBy(StringLiteral.fromUnescaped(separator, StringLiteral.Type.NONE));
        }

        public PrintValuesBuilder separateBySpace() {
            return separateBy(" ");
        }

        public PrintValuesBuilder endWith(StringLiteral end) {
            _end = end;
            return this;
        }

        public PrintValuesBuilder endWith(String end) {
            return endWith(StringLiteral.fromUnescaped(end, StringLiteral.Type.NONE));
        }

        public PrintValuesBuilder endWithNewline() {
            return endWith("\n");
        }

        public PrintValuesBuilder setValues(List<Expression> values) {
            _values = List.copyOf(values);
            return this;
        }

        public PrintValues build() {
            Objects.requireNonNull(_values);
            return new PrintValues(_values, _separator, _end);
        }
    }
}
