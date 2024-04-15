package org.vstu.meaningtree.nodes.types;

import org.vstu.meaningtree.nodes.Type;
import org.vstu.meaningtree.nodes.identifiers.SimpleIdentifier;

public class GenericUserType extends UserType implements Generic{
    private final Type[] _templateParameters;

    public GenericUserType(SimpleIdentifier name, Type ... templateParameters) {
        super(name);
        _templateParameters = templateParameters;
    }

    @Override
    public Type[] getTypeParameters() {
        return _templateParameters;
    }
}
