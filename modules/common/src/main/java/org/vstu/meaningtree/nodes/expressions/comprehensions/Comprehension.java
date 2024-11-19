package org.vstu.meaningtree.nodes.expressions.comprehensions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.vstu.meaningtree.nodes.Expression;

import java.util.Objects;

public abstract class Comprehension extends Expression {
    @NotNull
    protected ComprehensionItem _item;

    @Nullable
    protected Expression _condition;

    protected Comprehension(@NotNull ComprehensionItem item, @Nullable Expression condition) {
        _item = item;
        _condition = condition;
    }

    public interface ComprehensionItem extends Cloneable {
        ComprehensionItem clone();
    }

    public record KeyValuePair(Expression key, Expression value) implements ComprehensionItem {
        public KeyValuePair clone() {
            return new KeyValuePair(key.clone(), value.clone());
        }
    };

    public record ListItem(Expression value) implements ComprehensionItem {
        public ListItem clone() {
            return new ListItem(value.clone());
        }
    };

    public record SetItem(Expression value) implements ComprehensionItem {
        public SetItem clone() {
            return new SetItem(value.clone());
        }
    };

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
        return Objects.hash(super.hashCode(), _item, _condition);
    }

    @NotNull
    public Expression getCondition() {
        return Objects.requireNonNull(_condition, "Comprehension does not have condition");
    }

    @Override
    public Comprehension clone() {
        Comprehension obj = (Comprehension) super.clone();
        obj._condition = _condition.clone();
        obj._item = _item.clone();
        return obj;
    }
}
