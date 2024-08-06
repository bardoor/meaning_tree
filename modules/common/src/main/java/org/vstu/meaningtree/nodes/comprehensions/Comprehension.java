package org.vstu.meaningtree.nodes.comprehensions;

import org.vstu.meaningtree.nodes.Expression;

import java.util.Objects;
import java.util.Optional;

public abstract class Comprehension extends Expression {
    protected Comprehension(ComprehensionItem item, Expression condition) {
        _item = item;
        _condition = Optional.ofNullable(condition);
    }

    public interface ComprehensionItem {}
    public record KeyValuePair(Expression key, Expression value) implements ComprehensionItem {};
    public record ListItem(Expression value) implements ComprehensionItem {};
    public record SetItem(Expression value) implements ComprehensionItem {};

    protected final ComprehensionItem _item;
    protected final Optional<Expression> _condition;

    public ComprehensionItem getItem() {
        return _item;
    }

    public boolean hasCondition() {
        return _condition.isPresent();
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

    public Expression getCondition() {
        if (!hasCondition()) {
            throw new RuntimeException("Comprehension does not have condition");
        }

        return _condition.get();
    }
}
