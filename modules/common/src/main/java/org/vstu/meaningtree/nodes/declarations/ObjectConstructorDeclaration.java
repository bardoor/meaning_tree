package org.vstu.meaningtree.nodes.declarations;

import org.vstu.meaningtree.nodes.identifiers.Identifier;
import org.vstu.meaningtree.nodes.types.NoReturn;
import org.vstu.meaningtree.nodes.types.UserType;

import java.util.List;

public class ObjectConstructorDeclaration extends MethodDeclaration {

    public ObjectConstructorDeclaration(UserType owner, Identifier name,
                                        List<Annotation> annotations, List<Modifier> modifiers,
                                        DeclarationArgument... arguments) {
        super(owner, name, new NoReturn(), annotations, modifiers, arguments);
    }

    public ObjectConstructorDeclaration(UserType owner, Identifier name,
                                        List<Annotation> annotations, List<Modifier> modifiers,
                                        List<DeclarationArgument> arguments) {
        super(owner, name, new NoReturn(), annotations, modifiers, arguments);
    }
}
