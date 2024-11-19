package org.vstu.meaningtree.nodes.expressions.comprehensions;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.expressions.identifiers.SimpleIdentifier;
import org.vstu.meaningtree.nodes.expressions.other.Range;


public class RangeBasedComprehension extends Comprehension {
    private Range _range;
    private SimpleIdentifier _identifier;


    public RangeBasedComprehension(ComprehensionItem item, SimpleIdentifier rangeVariable, Range range, Expression condition) {
        super(item, condition);
        _identifier = rangeVariable;
        _range = range;
    }

    public SimpleIdentifier getRangeVariableIdentifier() {
        return _identifier;
    }

    public Range getRange() {return _range;}

    public RangeBasedComprehension clone() {
        RangeBasedComprehension obj = (RangeBasedComprehension) super.clone();
        obj._range = _range.clone();
        obj._identifier = _identifier.clone();
        return obj;
    }
}
