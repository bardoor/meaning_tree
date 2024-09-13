package org.vstu.meaningtree.nodes.modules;

import org.vstu.meaningtree.nodes.expressions.Identifier;

import java.util.List;

public class StaticImportMembers extends ImportMembers {

    public StaticImportMembers(Identifier scope, List<Identifier> members) {
        super(scope, members);
    }

    public StaticImportMembers(Identifier scope, Identifier... members) {
        super(scope, members);
    }
}
