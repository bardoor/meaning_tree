package org.vstu.meaningtree.nodes.types;

import org.vstu.meaningtree.nodes.Type;
import org.vstu.meaningtree.nodes.expressions.Identifier;
import org.vstu.meaningtree.nodes.interfaces.Generic;

import java.util.Arrays;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        GenericUserType that = (GenericUserType) o;
        return Objects.deepEquals(_templateParameters, that._templateParameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), Arrays.hashCode(_templateParameters));
    }
}
