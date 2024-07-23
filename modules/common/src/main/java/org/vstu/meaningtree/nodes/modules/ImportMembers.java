package org.vstu.meaningtree.nodes.modules;

import org.vstu.meaningtree.nodes.identifiers.Identifier;

import java.util.List;

public class ImportMembers extends Import {
    private final List<Identifier> _members;

    public ImportMembers(Identifier scope, List<Identifier> members) {
        super(scope);
        _members = List.copyOf(members);
    }

    public ImportMembers(Identifier scope, Identifier... members) {
        this(scope, List.of(members));
    }

    public List<Identifier> getMembers() {
        return _members;
    }
}
