package org.vstu.meaningtree.nodes.statements.conditions.components;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Statement;

public abstract class MatchValueCaseBlock extends CaseBlock {
    private final Expression _matchValue;

    public MatchValueCaseBlock(Expression matchValue, Statement body) {
        super(body);
        _matchValue = matchValue;
    }

    public Expression getMatchValue() {
        return _matchValue;
    }
}
