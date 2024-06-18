package org.vstu.meaningtree.nodes.statements;

import org.vstu.meaningtree.nodes.Statement;

import java.util.Arrays;
import java.util.List;

public class StatementSequence extends Statement {
    // Главным образом используется для оператора запятая в C++
    private final List<Statement> _statements;

    public StatementSequence(Statement ... stmts) {
        _statements = Arrays.asList(stmts);
    }

    public List<Statement> getStatements() {
        return _statements;
    }

    public boolean isOnlyAssignments() {
        return _statements.stream().allMatch((Statement stmt) -> stmt instanceof AssignmentStatement);
    }
}
