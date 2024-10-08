package org.vstu.meaningtree.nodes.interfaces;

import org.vstu.meaningtree.nodes.Statement;
import org.vstu.meaningtree.nodes.statements.CompoundStatement;
import org.vstu.meaningtree.utils.env.SymbolEnvironment;


public interface HasBodyStatement {
    Statement getBody();

    default boolean isCompound() {
        return getBody() instanceof CompoundStatement;
    }

    CompoundStatement makeCompoundBody(SymbolEnvironment env);
}
