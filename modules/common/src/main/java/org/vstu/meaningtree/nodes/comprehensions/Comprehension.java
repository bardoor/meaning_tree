package org.vstu.meaningtree.nodes.comprehensions;

import org.vstu.meaningtree.nodes.Expression;

public abstract class Comprehension {
    protected Comprehension(ComprehensionItem item) {
        this._item = item;
    }

    public interface ComprehensionItem {}
    public record KeyValuePair(Expression key, Expression value) implements ComprehensionItem {};
    public record ListItem(Expression value) implements ComprehensionItem {};

    protected final ComprehensionItem _item;

    public ComprehensionItem getItem() {
        return _item;
    }
}
