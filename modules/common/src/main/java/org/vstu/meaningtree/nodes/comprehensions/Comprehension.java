package org.vstu.meaningtree.nodes.comprehensions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.vstu.meaningtree.nodes.Expression;

import java.util.Objects;
import java.util.Optional;

public abstract class Comprehension extends Expression {
    @NotNull
    protected final ComprehensionItem _item;

    @Nullable
    protected final Expression _condition;

    protected Comprehension(@NotNull ComprehensionItem item, @Nullable Expression condition) {
        _item = item;
        _condition = condition;
    }

    public interface ComprehensionItem {}
    public record KeyValuePair(Expression key, Expression value) implements ComprehensionItem {};
    public record ListItem(Expression value) implements ComprehensionItem {};
    public record SetItem(Expression value) implements ComprehensionItem {};

    public ComprehensionItem getItem() {
        return _item;
    }

    public boolean hasCondition() {
        return _condition != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comprehension that = (Comprehension) o;
        return Objects.equals(_item, that._item) && Objects.equals(_condition, that._condition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_item, _condition);
    }

    @NotNull
    public Expression getCondition() {
        return Objects.requireNonNull(_condition, "Comprehension does not have condition");
    }
}
