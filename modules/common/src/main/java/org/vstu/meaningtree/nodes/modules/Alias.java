package org.vstu.meaningtree.nodes.modules;

import org.vstu.meaningtree.nodes.Identifier;
import org.vstu.meaningtree.nodes.identifiers.SimpleIdentifier;

public class Alias extends Identifier {
    private final SimpleIdentifier _realName;
    private final SimpleIdentifier _alias;

    public Alias(SimpleIdentifier realName, SimpleIdentifier alias) {
        _realName = realName;
        _alias = alias;
    }

    public SimpleIdentifier getRealName() {
        return _realName;
    }

    public SimpleIdentifier getAlias() {
        return _alias;
    }
}
