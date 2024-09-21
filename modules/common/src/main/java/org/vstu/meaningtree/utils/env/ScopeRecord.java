package org.vstu.meaningtree.utils.env;

import org.vstu.meaningtree.nodes.interfaces.HasSymbolScope;

public class ScopeRecord {
    public final int openingPosition;
    public final HasSymbolScope scope;

    public ScopeRecord(int openingPosition, HasSymbolScope scope) {
        this.openingPosition = openingPosition;
        this.scope = scope;
    }

    public HasSymbolScope getScope() {
        return scope;
    }

    public SymbolEnvironment getEnv() {
        return scope.getEnv();
    }
}
