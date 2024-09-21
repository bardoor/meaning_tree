package org.vstu.meaningtree.nodes.definitions;

import org.jetbrains.annotations.Nullable;
import org.vstu.meaningtree.nodes.Definition;
import org.vstu.meaningtree.nodes.Node;
import org.vstu.meaningtree.nodes.declarations.ClassDeclaration;
import org.vstu.meaningtree.nodes.declarations.FieldDeclaration;
import org.vstu.meaningtree.nodes.declarations.MethodDeclaration;
import org.vstu.meaningtree.nodes.enums.DeclarationModifier;
import org.vstu.meaningtree.nodes.statements.CompoundStatement;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ClassDefinition extends Definition {
    private final CompoundStatement _body;

    public ClassDefinition(ClassDeclaration declaration, CompoundStatement body) {
        super(declaration);
        this._body = body;
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }

    public List<Node> getFields() {
        return Arrays.stream(_body.getNodes()).filter((Node node) -> node instanceof FieldDeclaration).collect(Collectors.toList());
    }

    public List<Node> getMethods() {
        return Arrays.stream(_body.getNodes()).filter((Node node) -> node instanceof MethodDeclaration).collect(Collectors.toList());
    }

    public List<Node> getAllNodes() {
        return List.of(_body.getNodes());
    }

    public CompoundStatement getBody() {
        return _body;
    }

    @Nullable
    public MethodDefinition findMethod(String methodName) {
        for (Node node : _body) {
            if (!(node instanceof MethodDefinition methodDefinition)) {
                continue;
            }

            String name = methodDefinition.getName().getName();
            if (name.equals(methodName)) {
                return methodDefinition;
            }
        }

        return null;
    }

    public List<DeclarationModifier> getModifiers() {
        return ((ClassDeclaration) getDeclaration()).getModifiers();
    }
}
