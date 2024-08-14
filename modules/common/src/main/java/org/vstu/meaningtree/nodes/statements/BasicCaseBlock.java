package org.vstu.meaningtree.nodes.statements;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Statement;

public class BasicCaseBlock extends MatchValueCaseBlock {

    public BasicCaseBlock(Expression matchValue, Statement body) {
        super(matchValue, body);
    }
}
