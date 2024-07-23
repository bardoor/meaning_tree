package org.vstu.meaningtree.nodes.declarations;

import org.vstu.meaningtree.nodes.identifiers.Identifier;
import org.vstu.meaningtree.nodes.Type;
import org.vstu.meaningtree.nodes.identifiers.QualifiedIdentifier;
import org.vstu.meaningtree.nodes.identifiers.ScopedIdentifier;
import org.vstu.meaningtree.nodes.identifiers.SimpleIdentifier;

import java.util.ArrayList;
import java.util.List;

public class FunctionDeclaration extends Declaration {
    private final List<DeclarationArgument> _arguments;
    private final Identifier _name;
    private final Type _returnType;
    private final List<Annotation> _annotations;

    public FunctionDeclaration(Identifier name, Type returnType, List<Annotation> annotations, DeclarationArgument... arguments) {
        this(name, returnType, annotations, List.of(arguments));
    }

    public FunctionDeclaration(Identifier name, Type returnType, List<Annotation> annotations, List<DeclarationArgument> arguments) {
        _name = name;
        _annotations = new ArrayList<>(annotations);
        _arguments = List.copyOf(arguments);
        _returnType = returnType;
    }

    public Identifier getQualifiedName() {
        return _name;
    }

    public SimpleIdentifier getName() {
        if (getQualifiedName() instanceof QualifiedIdentifier qualified) {
            return qualified.getMember();
        } else if (getQualifiedName() instanceof ScopedIdentifier scoped) {
            return scoped.getScopeResolution().getLast();
        }
        return (SimpleIdentifier) _name;
    }

    public List<DeclarationArgument> getArguments() {
        return _arguments;
    }

    public Type getReturnType() {
        return _returnType;
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }

    public List<Annotation> getAnnotations() {
        return new ArrayList<>(_annotations);
    }

    public boolean hasAnnotations() {
        return !_annotations.isEmpty();
    }
}
