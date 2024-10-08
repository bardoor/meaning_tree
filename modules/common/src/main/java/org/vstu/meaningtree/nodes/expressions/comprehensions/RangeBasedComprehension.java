package org.vstu.meaningtree.nodes.expressions.comprehensions;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.expressions.other.Range;
import org.vstu.meaningtree.nodes.expressions.identifiers.SimpleIdentifier;


public class RangeBasedComprehension extends Comprehension {
    private final Range _range;
    private final SimpleIdentifier _identifier;


    public RangeBasedComprehension(ComprehensionItem item, SimpleIdentifier rangeVariable, Range range, Expression condition) {
        super(item, condition);
        _identifier = rangeVariable;
        _range = range;
    }

    public SimpleIdentifier getRangeVariableIdentifier() {
        return _identifier;
    }

    public Range getRange() {return _range;}
}
