package org.vstu.meaningtree.nodes.io;

import org.jetbrains.annotations.NotNull;
import org.vstu.meaningtree.iterators.utils.TreeNode;
import org.vstu.meaningtree.nodes.Expression;

import java.util.List;

public class PointerInputCommand extends InputCommand {
    @TreeNode private Expression target;

    public PointerInputCommand(@NotNull Expression target, @NotNull List<Expression> values) {
        super(values);
        this.target = target;
    }

    public Expression getTargetString() {
        return target;
    }
}
