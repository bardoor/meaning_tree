package org.vstu.meaningtree.nodes.io;

import org.jetbrains.annotations.NotNull;
import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.utils.TreeNode;

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
