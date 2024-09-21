package org.vstu.meaningtree.nodes.declarations;

import org.vstu.meaningtree.nodes.declarations.components.DeclarationArgument;
import org.vstu.meaningtree.nodes.enums.DeclarationModifier;
import org.vstu.meaningtree.nodes.expressions.Identifier;
import org.vstu.meaningtree.nodes.types.NoReturn;
import org.vstu.meaningtree.nodes.types.UserType;

import java.util.List;

public class ObjectConstructorDeclaration extends MethodDeclaration {

    public ObjectConstructorDeclaration(UserType owner, Identifier name,
                                        List<Annotation> annotations, List<DeclarationModifier> modifiers,
                                        DeclarationArgument... arguments) {
        super(owner, name, new NoReturn(), annotations, modifiers, arguments);
    }

    public ObjectConstructorDeclaration(UserType owner, Identifier name,
                                        List<Annotation> annotations, List<DeclarationModifier> modifiers,
                                        List<DeclarationArgument> arguments) {
        super(owner, name, new NoReturn(), annotations, modifiers, arguments);
    }
}
