package org.vstu.meaningtree.nodes.declarations;

import org.vstu.meaningtree.nodes.enums.DeclarationModifier;
import org.vstu.meaningtree.nodes.expressions.Identifier;
import org.vstu.meaningtree.nodes.types.NoReturn;
import org.vstu.meaningtree.nodes.types.UserType;

import java.util.List;

public class ObjectDestructorDeclaration extends MethodDeclaration {

    public ObjectDestructorDeclaration(UserType owner,
                                       Identifier name,
                                       List<Annotation> annotations,
                                       List<DeclarationModifier> modifiers) {
        super(owner, name, new NoReturn(), annotations, modifiers);
    }
}
