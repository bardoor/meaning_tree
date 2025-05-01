package org.vstu.meaningtree.nodes.statements.conditions.components;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Statement;
import org.vstu.meaningtree.utils.TreeNode;

public abstract class MatchValueCaseBlock extends CaseBlock {
    @TreeNode private Expression matchValue;

    public MatchValueCaseBlock(Expression matchValue, Statement body) {
        super(body);
        this.matchValue = matchValue;
    }

    public Expression getMatchValue() {
        return matchValue;
    }
}
