package org.vstu.meaningtree.nodes;

import org.vstu.meaningtree.nodes.expressions.Identifier;

import java.util.Objects;

public abstract class Type extends Identifier {
    private boolean _isConst = false;

    public boolean isConst() {
        return _isConst;
    }

    // Чтобы не пришлось модифицировать конструкторы остальных типов
    public void setConst(boolean state) {
        _isConst = state;
    }

    @Override
    public boolean equals(Object o) {
        return o.getClass().equals(this.getClass());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getClass().getName().hashCode(), "meaning_tree_type_node");
    }
}
