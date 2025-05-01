package org.vstu.meaningtree.nodes.expressions.comprehensions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.utils.TreeNode;

import java.util.Objects;

public abstract class Comprehension extends Expression {
    @TreeNode @NotNull protected ComprehensionItem item;

    @TreeNode @Nullable protected Expression condition;

    protected Comprehension(@NotNull ComprehensionItem item, @Nullable Expression condition) {
        this.item = item;
        this.condition = condition;
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
        return item;
    }

    public boolean hasCondition() {
        return condition != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comprehension that = (Comprehension) o;
        return Objects.equals(item, that.item) && Objects.equals(condition, that.condition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), item, condition);
    }

    @NotNull
    public Expression getCondition() {
        return Objects.requireNonNull(condition, "Comprehension does not have condition");
    }

    @Override
    public Comprehension clone() {
        Comprehension obj = (Comprehension) super.clone();
        obj.condition = condition.clone();
        obj.item = item.clone();
        return obj;
    }
}
