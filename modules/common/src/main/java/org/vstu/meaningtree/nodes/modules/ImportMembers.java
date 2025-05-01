package org.vstu.meaningtree.nodes.modules;

import org.vstu.meaningtree.nodes.expressions.Identifier;
import org.vstu.meaningtree.utils.TreeNode;

import java.util.List;

public class ImportMembers extends Import {
    @TreeNode private List<Identifier> members;

    public ImportMembers(Identifier scope, List<Identifier> members) {
        super(scope);
        this.members = List.copyOf(members);
    }

    public ImportMembers(Identifier scope, Identifier... members) {
        this(scope, List.of(members));
    }

    public List<Identifier> getMembers() {
        return members;
    }
}
