package org.vstu.meaningtree.utils.env.records;

import org.jetbrains.annotations.Nullable;
import org.vstu.meaningtree.nodes.declarations.FunctionDeclaration;
import org.vstu.meaningtree.nodes.definitions.FunctionDefinition;
import org.vstu.meaningtree.utils.env.SymbolRecord;

public class FunctionRecord extends SymbolRecord {
    private final FunctionDeclaration declaration;
    @Nullable
    protected FunctionDefinition definition = null;

    public FunctionRecord(int position, FunctionDeclaration declaration, @Nullable FunctionDefinition definition) {
        super(position);
        this.declaration = declaration;
    }

    public FunctionDeclaration getDeclaration() {
        return declaration;
    }

    public void setDefinition(FunctionDefinition def) {
        definition = def;
    }

    @Nullable
    public FunctionDefinition getDefinition() {
        return definition;
    }
}
