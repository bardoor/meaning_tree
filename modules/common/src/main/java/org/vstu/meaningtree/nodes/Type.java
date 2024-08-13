package org.vstu.meaningtree.nodes;

import java.util.Objects;

public abstract class Type extends Node {
    @Override
    public boolean equals(Object o) {
        return o.getClass().equals(this.getClass());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getClass().getName().hashCode(), "meaning_tree_type_node");
    }
}
