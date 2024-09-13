package org.vstu.meaningtree.nodes.types.user;

import org.vstu.meaningtree.nodes.Type;
import org.vstu.meaningtree.nodes.expressions.Identifier;
import org.vstu.meaningtree.nodes.types.GenericUserType;

public class GenericClass extends GenericUserType {
    public GenericClass(Identifier name, Type... templateParameters) {
        super(name, templateParameters);
    }
}
