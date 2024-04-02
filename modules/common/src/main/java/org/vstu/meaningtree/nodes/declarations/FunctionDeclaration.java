package org.vstu.meaningtree.nodes.declarations;

import org.vstu.meaningtree.nodes.Type;
import org.vstu.meaningtree.nodes.definitions.Argument;
import org.vstu.meaningtree.nodes.Identifier;

import java.util.List;
import java.util.Optional;

public class FunctionDeclaration extends Declaration {
    private final List<Argument> _arguments;
    private final Type _returnType;
    private final Optional<Annotation> annotation;

    public FunctionDeclaration(Identifier name, Type returnType, Annotation annotation, Argument ... arguments) {
        super(name);
        this.annotation = Optional.ofNullable(annotation);
        this._arguments = List.of(arguments);
        this._returnType = returnType;
    }

    public List<Argument> getArguments() {
        return _arguments;
    }

    public Type getReturnType() {
        return _returnType;
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }

    public Annotation getAnnotation() {
        if (!hasAnnotation()) {
            throw new RuntimeException("Annotation isn't present");
        }
        return annotation.get();
    }

    public boolean hasAnnotation() {
        return annotation.isPresent();
    }
}
