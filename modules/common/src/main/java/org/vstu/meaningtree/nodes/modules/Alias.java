package org.vstu.meaningtree.nodes.modules;

import org.vstu.meaningtree.iterators.utils.TreeNode;
import org.vstu.meaningtree.nodes.expressions.Identifier;
import org.vstu.meaningtree.nodes.expressions.identifiers.SimpleIdentifier;

public class Alias extends Identifier {
    @TreeNode private Identifier realName;
    @TreeNode private SimpleIdentifier alias;

    public Alias(Identifier realName, SimpleIdentifier alias) {
        this.realName = realName;
        this.alias = alias;
    }

    public Identifier getRealName() {
        return realName;
    }

    public SimpleIdentifier getAlias() {
        return alias;
    }

    @Override
    public boolean contains(Identifier other) {
        return alias.equals(other) || realName.contains(other);
    }

    @Override
    public int contentSize() {
        return realName.contentSize() + 1;
    }
}
