package org.vstu.meaningtree.languages.viewers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.vstu.meaningtree.nodes.Type;
import org.vstu.meaningtree.nodes.declarations.MethodDeclaration;
import org.vstu.meaningtree.nodes.identifiers.SimpleIdentifier;

import java.util.HashMap;
import java.util.Map;

public class Scope {
    private final Scope _parentScope;
    private final Map<SimpleIdentifier, Type> _variables;
    private final Map<SimpleIdentifier, Type> _methods;

    public Scope(@Nullable Scope parentScope) {
        _parentScope = parentScope;
        _variables = new HashMap<>();
        _methods = new HashMap<>();
    }

    public Scope() {
        this(null);
    }

    @Nullable
    public Scope getParentScope() {
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

    @Nullable
    public Type getMethodReturnType(@NotNull SimpleIdentifier methodName) {
        Type methodType = _methods.getOrDefault(methodName, null);

        if (methodType == null) {
            if (_parentScope == null) {
                return null;
            }

            methodType = _parentScope.getMethodReturnType(methodName);
        }

        return methodType;
    }
}
