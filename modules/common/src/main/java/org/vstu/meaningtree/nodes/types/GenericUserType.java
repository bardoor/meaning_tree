package org.vstu.meaningtree.nodes.types;

import org.vstu.meaningtree.nodes.Type;
import org.vstu.meaningtree.nodes.expressions.Identifier;
import org.vstu.meaningtree.nodes.interfaces.Generic;

public class GenericUserType extends UserType implements Generic {
    private final Type[] _templateParameters;

    public GenericUserType(Identifier name, Type ... templateParameters) {
        super(name);
        _templateParameters = templateParameters;
    }

    @Override
    public Type[] getTypeParameters() {
        return _templateParameters;
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
