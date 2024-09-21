package org.vstu.meaningtree.utils.env.records;

import org.jetbrains.annotations.Nullable;
import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.declarations.FieldDeclaration;

public class FieldRecord extends VariableRecord {
    protected final UserTypeRecord owner;

    public FieldRecord(int position, UserTypeRecord owner, FieldDeclaration decl, @Nullable Expression value) {
        super(position, decl, value);
        this.owner = owner;
    }

    public UserTypeRecord getOwner() {
        return owner;
    }

}
