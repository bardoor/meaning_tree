package org.vstu.meaningtree.nodes.comprehensions;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.identifiers.SimpleIdentifier;

public class RangeBasedComprehension extends Comprehension {
    private final Expression _start;
    private final Expression _end;
    private final Expression _step;
    private final SimpleIdentifier _identifier;

    public RangeBasedComprehension(ComprehensionItem item, SimpleIdentifier rangeVariable, Expression start, Expression end, Expression step, Expression condition) {
        super(item, condition);
        _start = start;
        _end = end;
        _step = step;
        _identifier = rangeVariable;
    }

    public Expression getStart() { return _start; }

    public Expression getEnd() { return _end; }

    public Expression getStep() { return _step; }

    public SimpleIdentifier getRangeVariableIdentifier() {
        return _identifier;
    }
}
