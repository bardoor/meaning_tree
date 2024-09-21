package org.vstu.meaningtree.languages.utils;

import org.vstu.meaningtree.nodes.Node;
import org.vstu.meaningtree.nodes.interfaces.Util;
import org.vstu.meaningtree.nodes.statements.CompoundStatement;
import org.vstu.meaningtree.utils.env.SymbolEnvironment;

public class PseudoCompoundStatement extends CompoundStatement implements Util {
    public PseudoCompoundStatement(Node... nodes) {
        super(new SymbolEnvironment(null), nodes);
    }
}
