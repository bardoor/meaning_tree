package org.vstu.meaningtree.nodes;

import org.vstu.meaningtree.nodes.definitions.ClassDefinition;

import java.util.List;

public class ProgramEntryPoint extends Node {

    /**
     * For script languages: all content of code (including comments)
     * For languages that require main function, or main class: content of main (entry point) function
     *
     * If main class with static fields and entry point function are separated (see docs for _globals field), developer must correctly access to this variables
     */
    private final List<Node> _body;

    /**
     * For script languages - empty list
     * For languages that require main function: global declarations and definitions
     * For languages that require main class that contains only static fields and static methods: static methods as functions (excluding main), static fields as global variables
     * For languages that require main class that also contains instance members (non-static fields and methods): main class with all fields excluding main function
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
