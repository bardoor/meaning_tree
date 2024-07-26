package org.vstu.meaningtree.nodes.definitions;

import org.vstu.meaningtree.nodes.declarations.Annotation;
import org.vstu.meaningtree.nodes.declarations.DeclarationArgument;
import org.vstu.meaningtree.nodes.declarations.MethodDeclaration;
import org.vstu.meaningtree.nodes.declarations.Modifier;
import org.vstu.meaningtree.nodes.identifiers.Identifier;
import org.vstu.meaningtree.nodes.statements.CompoundStatement;
import org.vstu.meaningtree.nodes.types.NoReturn;
import org.vstu.meaningtree.nodes.types.UserType;

import java.util.List;

public class ObjectConstructor extends MethodDefinition {

    public ObjectConstructor(
            UserType owner,
            Identifier name,
            List<Annotation> annotations,
            List<Modifier> modifiers,
            List<DeclarationArgument> arguments,
            CompoundStatement body
    ) {
        super(new MethodDeclaration(owner, name, new NoReturn(), annotations, modifiers, arguments), body);
    }
}
