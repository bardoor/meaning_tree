package org.vstu.meaningtree.nodes;

import org.vstu.meaningtree.nodes.definitions.ClassDefinition;
import org.vstu.meaningtree.nodes.definitions.FunctionDefinition;

import java.util.List;
import java.util.Optional;

public class ProgramEntryPoint extends Node {
    /**
     * Body has main class and entry point function,
     * but language viewer must convert entry point in proper format
     * (for example: without main class in C++)
     */
    private final List<Node> _body;
    private final Optional<FunctionDefinition> _entryPointFunction;
    private final Optional<ClassDefinition> _mainClass;

    public ProgramEntryPoint(List<Node> body, ClassDefinition mainClass) {
        this(body, mainClass, null);
    }

    public ProgramEntryPoint(List<Node> body) {
        this(body, null, null);
    }

    public ProgramEntryPoint(List<Node> body, FunctionDefinition entryPointFunction) {
        this(body, null, null);
    }

    public ProgramEntryPoint(List<Node> body, ClassDefinition mainClass, FunctionDefinition entryPointFunction) {
        _body = body;
        _mainClass = Optional.ofNullable(mainClass);
        _entryPointFunction = Optional.ofNullable(entryPointFunction);
    }

    public List<Node> getBody() {
        return _body;
    }

    public ClassDefinition getMainClass() {
        if (!hasMainClass()) {
            throw new RuntimeException("Main class is not present");
        }
        return _mainClass.get();
    }

    public boolean hasMainClass() {
        return _mainClass.isPresent();
    }

    public boolean hasEntryPointFunction() {
        return _entryPointFunction.isPresent();
    }

    public FunctionDefinition getEntryPointFunction() {
        if (!hasEntryPointFunction()) {
            throw new RuntimeException("Main class is not present");
        }
        return _entryPointFunction.get();
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
