package org.vstu.meaningtree.nodes.definitions;

import org.vstu.meaningtree.nodes.Definition;
import org.vstu.meaningtree.nodes.declarations.MethodDeclaration;
import org.vstu.meaningtree.nodes.expressions.identifiers.SimpleIdentifier;
import org.vstu.meaningtree.nodes.statements.CompoundStatement;
import org.vstu.meaningtree.nodes.interfaces.HasBodyStatement;

public class MethodDefinition extends Definition implements HasBodyStatement {
    private CompoundStatement _body;

    public MethodDefinition(MethodDeclaration declaration, CompoundStatement body) {
        super(declaration);
        _body = body;
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }

    public CompoundStatement getBody() {
        return _body;
    }

    @Override
    public void makeBodyCompound() {}

    public SimpleIdentifier getName() {
        MethodDeclaration methodDeclaration = (MethodDeclaration) getDeclaration();
        return methodDeclaration.getName();
    }
}
