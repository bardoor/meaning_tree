package org.vstu.meaningtree.nodes.types;

import org.vstu.meaningtree.nodes.Type;
import org.vstu.meaningtree.nodes.identifiers.SimpleIdentifier;

import java.util.Optional;

public class UserType extends Type {
    private final SimpleIdentifier _name;

    public SimpleIdentifier getName() {
        return _name;
    }


    public UserType(SimpleIdentifier name) {
        _name = name;
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
