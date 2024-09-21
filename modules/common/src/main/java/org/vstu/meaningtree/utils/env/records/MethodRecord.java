package org.vstu.meaningtree.utils.env.records;

import org.jetbrains.annotations.Nullable;
import org.vstu.meaningtree.nodes.declarations.MethodDeclaration;
import org.vstu.meaningtree.nodes.definitions.FunctionDefinition;
import org.vstu.meaningtree.nodes.definitions.MethodDefinition;

public class MethodRecord extends FunctionRecord {
    protected final UserTypeRecord owner;

    public MethodRecord(int position, UserTypeRecord owner, MethodDeclaration declaration, @Nullable FunctionDefinition definition) {
        super(position, declaration, definition);
        this.owner = owner;
    }

    public UserTypeRecord getOwner() {
        return owner;
    }

    public void setDefinition(MethodDefinition def) {
        definition = def;
    }
}
