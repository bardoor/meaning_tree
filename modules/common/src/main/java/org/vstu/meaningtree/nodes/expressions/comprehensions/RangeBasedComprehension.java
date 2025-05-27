package org.vstu.meaningtree.nodes.expressions.comprehensions;

import org.vstu.meaningtree.iterators.utils.TreeNode;
import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.expressions.identifiers.SimpleIdentifier;
import org.vstu.meaningtree.nodes.expressions.other.Range;

import java.util.Objects;


public class RangeBasedComprehension extends Comprehension {
    @TreeNode private Range range;
    @TreeNode private SimpleIdentifier identifier;


    public RangeBasedComprehension(ComprehensionItem item, SimpleIdentifier rangeVariable, Range range, Expression condition) {
        super(item, condition);
        identifier = rangeVariable;
        this.range = range;
    }

    public SimpleIdentifier getRangeVariableIdentifier() {
        return identifier;
    }

    public Range getRange() {return range;}

    public RangeBasedComprehension clone() {
        RangeBasedComprehension obj = (RangeBasedComprehension) super.clone();
        obj.range = range.clone();
        obj.identifier = identifier.clone();
        return obj;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), range, identifier);
    }
}
