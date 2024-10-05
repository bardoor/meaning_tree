package org.vstu.meaningtree.nodes;

import org.vstu.meaningtree.nodes.expressions.Identifier;

import java.util.Objects;

public abstract class Type extends Identifier {
    @Override
    public boolean equals(Object o) {
        return o.getClass().equals(this.getClass());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getClass().getName().hashCode(), "meaning_tree_type_node");
    }
}
