package org.vstu.meaningtree.nodes;

public class PackageDeclaration extends Statement {
    private final Identifier _packageName;

    public PackageDeclaration(Identifier packageName) {
        _packageName = packageName;
    }

    public Identifier getPackageName() {
        return _packageName;
    }
}
