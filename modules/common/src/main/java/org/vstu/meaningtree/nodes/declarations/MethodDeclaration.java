package org.vstu.meaningtree.nodes.declarations;

import org.vstu.meaningtree.nodes.Type;
import org.vstu.meaningtree.nodes.definitions.Argument;
import org.vstu.meaningtree.nodes.Identifier;

public class MethodDeclaration extends FunctionDeclaration {
    private final Type _owner;

    public MethodDeclaration(Type owner, Identifier name, Type returnType, Annotation annotation, Argument... arguments) {
        super(name, returnType, annotation, arguments);
        _owner = owner;
    }
}
