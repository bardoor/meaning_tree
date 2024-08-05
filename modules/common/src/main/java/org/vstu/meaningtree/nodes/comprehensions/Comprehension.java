package org.vstu.meaningtree.nodes.comprehensions;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Node;

import java.util.Optional;

public abstract class Comprehension extends Node {
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

    public Expression getCondition() {
        if (!hasCondition()) {
            throw new RuntimeException("Comprehension does not have condition");
        }

        return _condition.get();
    }
}
