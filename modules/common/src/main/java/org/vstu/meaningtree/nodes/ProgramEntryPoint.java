package org.vstu.meaningtree.nodes;

import org.jetbrains.annotations.Nullable;
import org.vstu.meaningtree.nodes.definitions.ClassDefinition;
import org.vstu.meaningtree.nodes.interfaces.HasSymbolScope;
import org.vstu.meaningtree.utils.env.SymbolEnvironment;

import java.util.List;
import java.util.Objects;

public class ProgramEntryPoint extends Node implements HasSymbolScope {
    /**
     * _body не содержит _entryPointNode, если только точка входа - не метод главного класса.
     * Однако _entryPointNode может отсутствовать, тогда точка входа - _body
     * Viewer должен сам подстроиться под эту ситуацию и адаптировать под особенности своего языка
     */
    private final List<Node> _body;
    @Nullable
    private final SymbolEnvironment _env;

    /**
     * Может быть функцией, методом главного класса, либо просто составным оператором (например, как в Python)
     */
    @Nullable
    private final Node _entryPointNode;

    /**
     * Ссылка на главный класс. Он не исключается из body, нужен для удобства разработчиков поддержки для языков
     */
    @Nullable
    private final ClassDefinition _mainClass;

    public ProgramEntryPoint(@Nullable SymbolEnvironment env, List<Node> body, ClassDefinition mainClass) {
        this(env, body, mainClass, null);
    }

    public ProgramEntryPoint(@Nullable SymbolEnvironment env, List<Node> body) {
        this(env, body, null, null);
    }

    public ProgramEntryPoint(@Nullable SymbolEnvironment env, List<Node> body, Node entryPoint) {
        this(env, body, null, entryPoint);
    }

    public ProgramEntryPoint(@Nullable SymbolEnvironment env, List<Node> body, @Nullable ClassDefinition mainClass, @Nullable Node entryPoint) {
        _body = body;
        _mainClass = mainClass;
        _entryPointNode = entryPoint;
        _env = env;
    }

    public List<Node> getBody() {
        return _body;
    }

    public ClassDefinition getMainClass() {
        return Objects.requireNonNull(_mainClass, "Main class is not present");
    }

    public boolean hasMainClass() {
        return _mainClass != null;
    }

    public boolean hasEntryPoint() {
        return _entryPointNode != null;
    }

    public Node getEntryPoint() {
        return Objects.requireNonNull(_entryPointNode, "Main class is not present");
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

    @Override
    public SymbolEnvironment getEnv() {
        return _env;
    }
}
