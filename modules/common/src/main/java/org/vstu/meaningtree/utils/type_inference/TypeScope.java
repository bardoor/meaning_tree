package org.vstu.meaningtree.utils.type_inference;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.vstu.meaningtree.nodes.Type;
import org.vstu.meaningtree.nodes.expressions.identifiers.SimpleIdentifier;
import org.vstu.meaningtree.nodes.types.UnknownType;

import java.util.HashMap;
import java.util.Map;

public class TypeScope {
    private final TypeScope _parentScope;
    private final Map<SimpleIdentifier, Type> _variables;
    private final Map<SimpleIdentifier, Type> _methods;

    public TypeScope(@Nullable TypeScope parentScope) {
        _parentScope = parentScope;
        _variables = new HashMap<>();
        _methods = new HashMap<>();
    }

    public TypeScope() {
        this(null);
    }

    @Nullable
    public TypeScope getParentScope() {
        return _parentScope;
    }

    public void addVariable(@NotNull SimpleIdentifier variableName, @NotNull Type type) {
        _variables.put(variableName, type);
    }

    public void addMethod(@NotNull SimpleIdentifier methodName, @NotNull Type returnType) {
        _methods.put(methodName, returnType);
    }

    @Nullable
    public Type getVariableType(@NotNull SimpleIdentifier variableName) {
        Type variableType = _variables.getOrDefault(variableName, null);

        if (variableType == null) {
            if (_parentScope == null) {
                return null;
            }

            variableType = _parentScope.getVariableType(variableName);
        }

        return variableType;
    }

    public void changeVariableType(
            @NotNull SimpleIdentifier variableName,
            @NotNull Type type,
            boolean createIfNotExists
        ) {
        if (getVariableType(variableName) == null && !createIfNotExists) {
            throw new IllegalArgumentException("No such variable found");
        }

        _variables.put(variableName, type);
    }

    public void changeVariableType(
            @NotNull SimpleIdentifier variableName,
            @NotNull Type type
    ) {
        changeVariableType(variableName, type, true);
    }

    @NotNull
    public Type getMethodReturnType(@NotNull SimpleIdentifier methodName) {
        Type methodType = _methods.getOrDefault(methodName, null);

        if (methodType == null) {
            if (_parentScope == null) {
                return new UnknownType();
            }

            methodType = _parentScope.getMethodReturnType(methodName);
        }

        return methodType;
    }

    @NotNull
    public Map<SimpleIdentifier, Type> getVariables() {
        return _variables;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (var entry : _variables.entrySet()) {
            builder.append(
                    "%s = %s, ".formatted(entry.getKey(), entry.getValue())
            );
        }

        if (builder.length() > 2) {
            builder.deleteCharAt(builder.length() - 1);
            builder.deleteCharAt(builder.length() - 1);
        }

        return builder.toString();
    }
}
