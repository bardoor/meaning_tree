package org.vstu.meaningtree.nodes.declarations;

import org.vstu.meaningtree.nodes.Identifier;
import org.vstu.meaningtree.nodes.Type;
import org.vstu.meaningtree.nodes.identifiers.QualifiedIdentifier;
import org.vstu.meaningtree.nodes.identifiers.ScopedIdentifier;
import org.vstu.meaningtree.nodes.identifiers.SimpleIdentifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FunctionDeclaration extends Declaration {
    private final List<DeclarationArgument> _arguments;
    private final Identifier _name;
    private final Type _returnType;
    private final List<Annotation> annotations;

    public FunctionDeclaration(Identifier name, Type returnType, List<Annotation> annotations, DeclarationArgument... arguments) {
        _name = name;
        this.annotations = new ArrayList<>(annotations);
        this._arguments = List.of(arguments);
        this._returnType = returnType;
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
        return new ArrayList<>(annotations);
    }

    public boolean hasAnnotations() {
        return !annotations.isEmpty();
    }
}
