package org.vstu.meaningtree.nodes.definitions;

import org.vstu.meaningtree.nodes.declarations.*;
import org.vstu.meaningtree.nodes.identifiers.Identifier;
import org.vstu.meaningtree.nodes.statements.CompoundStatement;
import org.vstu.meaningtree.nodes.types.UserType;

import java.util.List;

public class ObjectConstructorDefinition extends MethodDefinition {

    public ObjectConstructorDefinition(
            UserType owner,
            Identifier name,
            List<Annotation> annotations,
            List<Modifier> modifiers,
            List<DeclarationArgument> arguments,
            CompoundStatement body
    ) {
        super(new ObjectConstructorDeclaration(owner, name, annotations, modifiers, arguments), body);
    }
}
