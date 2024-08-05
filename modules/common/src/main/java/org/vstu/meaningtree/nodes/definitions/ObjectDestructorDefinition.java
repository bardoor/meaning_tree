package org.vstu.meaningtree.nodes.definitions;

import org.vstu.meaningtree.nodes.declarations.Annotation;
import org.vstu.meaningtree.nodes.declarations.Modifier;
import org.vstu.meaningtree.nodes.declarations.ObjectDestructorDeclaration;
import org.vstu.meaningtree.nodes.identifiers.Identifier;
import org.vstu.meaningtree.nodes.statements.CompoundStatement;
import org.vstu.meaningtree.nodes.types.UserType;

import java.util.List;

public class ObjectDestructorDefinition extends MethodDefinition {
    public ObjectDestructorDefinition(UserType owner,
                                      Identifier name,
                                      List<Annotation> annotations,
                                      List<Modifier> modifiers, CompoundStatement body) {
        super(new ObjectDestructorDeclaration(owner, name, annotations, modifiers), body);
    }
}
