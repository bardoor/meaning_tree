package org.vstu.meaningtree.nodes.types;

import org.vstu.meaningtree.nodes.Type;
import org.vstu.meaningtree.nodes.identifiers.SimpleIdentifier;

public class GenericInterface extends GenericUserType {
    public GenericInterface(SimpleIdentifier name, Type... templateParameters) {
        super(name, templateParameters);
    }
}
