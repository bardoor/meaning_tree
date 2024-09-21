package org.vstu.meaningtree.utils.env.records;

import org.jetbrains.annotations.Nullable;
import org.vstu.meaningtree.nodes.declarations.ClassDeclaration;
import org.vstu.meaningtree.nodes.definitions.ClassDefinition;
import org.vstu.meaningtree.utils.env.SymbolEnvironment;

public class ClassRecord extends UserTypeRecord {
    private final ClassDeclaration declaration;
    @Nullable
    protected ClassDefinition definition = null;

    public ClassRecord(@Nullable SymbolEnvironment env, int position, ClassDeclaration declaration, @Nullable ClassDefinition definition) {
        super(env, position);
        this.declaration = declaration;
    }

    public ClassDeclaration getDeclaration() {
        return declaration;
    }

    public void setDefinition(ClassDefinition def) {
        definition = def;
    }

    @Nullable
    public ClassDefinition getDefinition() {
        return definition;
    }
}
