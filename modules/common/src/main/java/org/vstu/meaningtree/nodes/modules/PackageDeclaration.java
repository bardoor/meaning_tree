package org.vstu.meaningtree.nodes.modules;

import org.vstu.meaningtree.nodes.Declaration;
import org.vstu.meaningtree.nodes.expressions.Identifier;
import org.vstu.meaningtree.utils.TreeNode;

public class PackageDeclaration extends Declaration {
    @TreeNode private Identifier packageName;

    public PackageDeclaration(Identifier packageName) {
        this.packageName = packageName;
    }

    public Identifier getPackageName() {
        return packageName;
    }
}
