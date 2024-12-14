package org.vstu.meaningtree.nodes.modules;

import org.vstu.meaningtree.nodes.expressions.Identifier;
import org.vstu.meaningtree.nodes.expressions.identifiers.SimpleIdentifier;

public class Alias extends Identifier {
    private final Identifier _realName;
    private final SimpleIdentifier _alias;

    public Alias(Identifier realName, SimpleIdentifier alias) {
        _realName = realName;
        _alias = alias;
    }

    public Identifier getRealName() {
        return _realName;
    }

    public SimpleIdentifier getAlias() {
        return _alias;
    }

    @Override
    public boolean contains(Identifier other) {
        return _alias.equals(other) || _realName.contains(other);
    }

    @Override
    public int contentSize() {
        return _realName.contentSize() + 1;
    }
}
