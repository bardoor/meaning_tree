package org.vstu.meaningtree.nodes.definitions;

import org.vstu.meaningtree.nodes.Statement;
import org.vstu.meaningtree.nodes.declarations.FunctionDeclaration;

public class FunctionDefinition extends Definition {
    private Statement _body;

    public FunctionDefinition(FunctionDeclaration declaration, Statement body) {
        super(declaration);
        _body = body;
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }

    public Statement getBody() {
        return _body;
    }
}