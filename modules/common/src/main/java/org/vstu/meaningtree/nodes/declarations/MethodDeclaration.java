package org.vstu.meaningtree.nodes.declarations;

import org.vstu.meaningtree.enums.DeclarationModifier;
import org.vstu.meaningtree.nodes.declarations.components.DeclarationArgument;
import org.vstu.meaningtree.nodes.expressions.Identifier;
import org.vstu.meaningtree.nodes.Type;
import org.vstu.meaningtree.nodes.types.UserType;

import java.util.List;

public class MethodDeclaration extends FunctionDeclaration {
    private final UserType _owner;
    private final List<DeclarationModifier> _modifiers;

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
        _owner = owner;
        _modifiers = List.copyOf(modifiers);
    }

    public UserType getOwner() {
        return _owner;
    }

    public List<DeclarationModifier> getModifiers() {
        return _modifiers;
    }
}
