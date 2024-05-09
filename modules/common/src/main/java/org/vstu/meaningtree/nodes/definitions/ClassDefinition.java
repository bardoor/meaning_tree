package org.vstu.meaningtree.nodes.definitions;

import org.vstu.meaningtree.nodes.Node;
import org.vstu.meaningtree.nodes.declarations.ClassDeclaration;
import org.vstu.meaningtree.nodes.declarations.FieldDeclaration;
import org.vstu.meaningtree.nodes.declarations.MethodDeclaration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ClassDefinition extends Definition {
    private final List<Node> _body;

    public ClassDefinition(ClassDeclaration declaration, List<Node> body) {
        super(declaration);
        this._body = body;
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }

    public List<Node> getFields() {
        return _body.stream().filter((Node node) -> node instanceof FieldDeclaration).collect(Collectors.toList());
    }

    public List<Node> getMethods() {
        return _body.stream().filter((Node node) -> node instanceof MethodDeclaration).collect(Collectors.toList());
    }

    public List<Node> getAllNodes() {
        return new ArrayList<>(_body);
    }
}
