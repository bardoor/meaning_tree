package org.vstu.meaningtree.nodes;

import org.vstu.meaningtree.nodes.definitions.ClassDefinition;
import org.vstu.meaningtree.nodes.definitions.FunctionDefinition;

import java.util.List;
import java.util.Optional;

public class ProgramEntryPoint extends Node {
    /**
     * _body не содержит _entryPointNode, если только точка входа - не метод главного класса.
     * Однако _entryPointNode может отсутствовать, тогда точка входа - _body
     * Viewer должен сам подстроиться под эту ситуацию и адаптировать под особенности своего языка
     */
    private final List<Node> _body;

    /**
     * Может быть функцией, методом главного класса, либо просто составным оператором (например, как в Python)
     */
    private final Optional<Node> _entryPointNode;

    /**
     * Ссылка на главный класс. Он не исключается из body, нужен для удобства разработчиков поддержки для языков
     */
    private final Optional<ClassDefinition> _mainClass;

    public ProgramEntryPoint(List<Node> body, ClassDefinition mainClass) {
        this(body, mainClass, null);
    }

    public ProgramEntryPoint(List<Node> body) {
        this(body, null, null);
    }

    public ProgramEntryPoint(List<Node> body, Node entryPoint) {
        this(body, null, entryPoint);
    }

    public ProgramEntryPoint(List<Node> body, ClassDefinition mainClass, Node entryPoint) {
        _body = body;
        _mainClass = Optional.ofNullable(mainClass);
        _entryPointNode = Optional.ofNullable(entryPoint);
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

    public boolean hasEntryPoint() {
        return _entryPointNode.isPresent();
    }

    public Node getEntryPoint() {
        if (!hasEntryPoint()) {
            throw new RuntimeException("Main class is not present");
        }
        return _entryPointNode.get();
    }

    @Override
    public String generateDot() {
        StringBuilder builder = new StringBuilder();

        builder.append(String.format("%s [label=\"%s\"];\n", _id, getClass().getSimpleName()));
        for (var node : _body) {
            builder.append(node.generateDot());
            builder.append(String.format("%s -- %s;\n", _id, node.getId()));
        }

        return builder.toString();
    }
}
