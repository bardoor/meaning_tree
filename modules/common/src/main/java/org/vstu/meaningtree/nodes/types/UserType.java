package org.vstu.meaningtree.nodes.types;

import org.vstu.meaningtree.nodes.Type;
import org.vstu.meaningtree.nodes.Identifier;

import java.util.Optional;

public class UserType extends Type {
    private final Identifier _name;

    public Identifier getName() {
        return _name;
    }

    public Type getTemplateType() {
        if (!hasTemplateType()) {
            throw new RuntimeException("Type hasn't template");
        }
        return _templateType.get();
    }

    public boolean hasTemplateType() {
        return _templateType.isPresent();
    }

    private final Optional<Type> _templateType;

    public UserType(Identifier name, Type templateType) {
        _name = name;
        _templateType = Optional.ofNullable(templateType);
    }

    public UserType(Identifier name) {
        _name = name;
        _templateType = Optional.ofNullable(null);
    }
}
