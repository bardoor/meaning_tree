package org.vstu.meaningtree.nodes.types;

import org.vstu.meaningtree.nodes.Type;
import org.vstu.meaningtree.nodes.identifiers.SimpleIdentifier;

import java.util.Optional;

public class UserType extends Type {
    private final SimpleIdentifier _name;

    public SimpleIdentifier getName() {
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

    public UserType(SimpleIdentifier name, Type templateType) {
        _name = name;
        _templateType = Optional.ofNullable(templateType);
    }

    public UserType(SimpleIdentifier name) {
        _name = name;
        _templateType = Optional.ofNullable(null);
    }
}
