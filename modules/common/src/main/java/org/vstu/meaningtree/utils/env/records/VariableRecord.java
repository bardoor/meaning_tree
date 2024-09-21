package org.vstu.meaningtree.utils.env.records;

import org.jetbrains.annotations.Nullable;
import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.declarations.VariableDeclaration;
import org.vstu.meaningtree.utils.env.SymbolRecord;

import java.util.ArrayList;
import java.util.List;

public class VariableRecord extends SymbolRecord {
    @Nullable
    // это нужно так как, в declaration может быть несколько объявлений
    private Expression rvalue = null;

    private final VariableDeclaration declaration;

    private List<Integer> assignmentPositions = new ArrayList<>();

    public VariableRecord(int position, VariableDeclaration decl, @Nullable Expression value) {
        super(position);
        declaration = decl;
        rvalue = value;
    }

    public Expression getRValue() {
        return rvalue;
    }

    public void setRValue(Expression value) {
        rvalue = value;
    }

    public void putAssignmentPosition(int pos) {
        assignmentPositions.add(pos);
    }

    public Integer[] getAssignmentPositions() {
        return assignmentPositions.toArray(new Integer[0]);
    }

    public VariableDeclaration getDeclaration() {
        return declaration;
    }
}
