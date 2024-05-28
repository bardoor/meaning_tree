package org.vstu.meaningtree.nodes.statements;

import org.vstu.meaningtree.nodes.Identifier;
import org.vstu.meaningtree.nodes.Statement;

public class PackageDeclaration extends Statement {
    private final Identifier _packageName;

    public PackageDeclaration(Identifier packageName) {
        _packageName = packageName;
    }

    public Identifier getPackageName() {
        return _packageName;
    }
}
