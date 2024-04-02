package org.vstu.meaningtree.nodes;

import org.vstu.meaningtree.nodes.definitions.ClassDefinition;

import java.util.List;

public class ProgramEntryPoint extends Node {

    /**
     * For script languages: all content of code
     * For languages that require main function, or main class: content of main function
     *
     * Developer must keep in mind implicit access to main class static fields and methods in main function body and find them in main class if it is required
     */
    private final List<Node> _body;

    /**
     * For script languages - empty list
     * For languages that require main function, or main class: declarations, definitions, comments.
     * Main class, if it's present in code, should be included in globals without main function
     */
    private final List<Node> _globals;


    /**
     * Link to a main class for developer convenience
     * Conversion between languages with main class and other languages must remove main class if it is unnecessary
     */
    private final ClassDefinition _mainClass;

    public ProgramEntryPoint(List<Node> body, List<Node> globals, ClassDefinition mainClass) {
        _body = body;
        _globals = globals;
        _mainClass = mainClass;
    }

    public List<Node> getBody() {
        return _body;
    }

    public List<Node> getGlobals() {
        return _globals;
    }

    public ClassDefinition getMainClass() {
        return _mainClass;
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
