package org.vstu.meaningtree.nodes.modules;

import org.vstu.meaningtree.nodes.expressions.Identifier;
import org.vstu.meaningtree.nodes.Declaration;

public class PackageDeclaration extends Declaration {
    private final Identifier _packageName;

    public PackageDeclaration(Identifier packageName) {
        _packageName = packageName;
    }

    public Identifier getPackageName() {
        return _packageName;
    }
}
