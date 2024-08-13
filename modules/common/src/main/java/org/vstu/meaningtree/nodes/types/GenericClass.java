package org.vstu.meaningtree.nodes.types;

import org.vstu.meaningtree.nodes.Type;
import org.vstu.meaningtree.nodes.identifiers.Identifier;

public class GenericClass extends GenericUserType {
    public GenericClass(Identifier name, Type... templateParameters) {
        super(name, templateParameters);
    }
}
