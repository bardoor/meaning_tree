package org.vstu.meaningtree.nodes.declarations;

import org.vstu.meaningtree.nodes.Type;
import org.vstu.meaningtree.nodes.declarations.components.DeclarationArgument;
import org.vstu.meaningtree.nodes.enums.DeclarationModifier;
import org.vstu.meaningtree.nodes.expressions.Identifier;
import org.vstu.meaningtree.nodes.types.UserType;
import org.vstu.meaningtree.utils.TreeNode;

import java.util.List;

public class MethodDeclaration extends FunctionDeclaration {
    @TreeNode private UserType owner;
    private List<DeclarationModifier> modifiers;

    public MethodDeclaration(UserType owner,
                             Identifier name,
                             Type returnType,
                             List<Annotation> annotations,
                             List<DeclarationModifier> modifiers,
                             DeclarationArgument... arguments
    ) {
        this(owner, name, returnType, annotations, modifiers, List.of(arguments));
    }

    public MethodDeclaration(UserType owner,
                             Identifier name,
                             Type returnType,
                             List<Annotation> annotations,
                             List<DeclarationModifier> modifiers,
                             List<DeclarationArgument> arguments
    ) {
        super(name, returnType, annotations, arguments);
        this.owner = owner;
        this.modifiers = List.copyOf(modifiers);
    }

    public UserType getOwner() {
        return owner;
    }

    public List<DeclarationModifier> getModifiers() {
        return modifiers;
    }
}
