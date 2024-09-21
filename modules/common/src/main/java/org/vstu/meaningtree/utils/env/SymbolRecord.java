package org.vstu.meaningtree.utils.env;

public abstract class SymbolRecord {
    public final int declarationPosition;

    protected SymbolRecord(int declarationPosition) {
        this.declarationPosition = declarationPosition;
    }
}
