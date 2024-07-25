package org.vstu.meaningtree.nodes.statements;

import org.vstu.meaningtree.nodes.Statement;

import java.util.Arrays;
import java.util.List;

public class MultipleAssignmentStatement extends Statement {
    // Множественное присваивание (применяется в Python)
    private final List<AssignmentStatement> _statements;

    public MultipleAssignmentStatement(AssignmentStatement ... stmts) {
        this(List.of(stmts));
    }

    public MultipleAssignmentStatement(List<AssignmentStatement> stmts) {
        _statements = List.copyOf(stmts);
    }

    public List<AssignmentStatement> getStatements() {
        return _statements;
    }
}
