package org.vstu.meaningtree.nodes.definitions;

import org.vstu.meaningtree.nodes.declarations.ClassDeclaration;
import org.vstu.meaningtree.nodes.declarations.FieldDeclaration;

import java.util.List;

public class ClassDefinition extends Definition {
    private final List<FieldDeclaration> _fields;
    private final List<MethodDefinition> _methods;

    public ClassDefinition(ClassDeclaration declaration, List<FieldDeclaration> fields, List<MethodDefinition> methods) {
        super(declaration);
        this._fields = fields;
        this._methods = methods;
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }

    public List<FieldDeclaration> getFields() {
        return _fields;
    }

    public List<MethodDefinition> getMethods() {
        return _methods;
    }
}
