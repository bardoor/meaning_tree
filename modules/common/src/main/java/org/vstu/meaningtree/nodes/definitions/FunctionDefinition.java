package org.vstu.meaningtree.nodes.definitions;

import org.vstu.meaningtree.nodes.Identifier;
import org.vstu.meaningtree.nodes.Statement;
import org.vstu.meaningtree.nodes.Type;
import org.vstu.meaningtree.nodes.declarations.DeclarationArgument;
import org.vstu.meaningtree.nodes.declarations.FunctionDeclaration;
import org.vstu.meaningtree.nodes.declarations.MethodDeclaration;
import org.vstu.meaningtree.nodes.declarations.VisibilityModifier;
import org.vstu.meaningtree.nodes.statements.CompoundStatement;
import org.vstu.meaningtree.nodes.statements.HasBodyStatement;
import org.vstu.meaningtree.nodes.types.UserType;

public class FunctionDefinition extends Definition implements HasBodyStatement {
    private Statement _body;

    public FunctionDefinition(FunctionDeclaration declaration, Statement body) {
        super(declaration);
        _body = body;
    }

    public Identifier getName() {
        return ((FunctionDeclaration) getDeclaration()).getName();
    }

    public MethodDefinition makeMethod(UserType owner, boolean isStatic, VisibilityModifier modifier) {
        FunctionDeclaration decl = (FunctionDeclaration) getDeclaration();
        return new MethodDefinition(
                new MethodDeclaration(
                        owner, decl.getName(), decl.getReturnType(),
                        decl.getAnnotation(), modifier, isStatic,
                        decl.getArguments().toArray(new DeclarationArgument[0])),
                getBody());
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }

    public Statement getBody() {
        return _body;
    }

    @Override
    public void makeBodyCompound() {
        assert getBody() instanceof CompoundStatement;
    }
}