package org.vstu.meaningtree.nodes.statements;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Statement;

public class FallthroughCaseBlock extends MatchValueCaseBlock {

    public FallthroughCaseBlock(Expression matchValue, Statement body) {
        super(matchValue, body);
    }
}
