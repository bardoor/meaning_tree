package org.vstu.meaningtree;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Identifier;
import org.vstu.meaningtree.nodes.Statement;

import java.util.Optional;

public class VariableDeclaration extends Statement {
    protected final Type _type;
    protected final Identifier _name;
    protected final Optional<Expression> _rvalue;

    public VariableDeclaration(Type type, Identifier name) {
        this(type, name, null);
    }

    public VariableDeclaration(Type type, Identifier name, Expression rvalue) {
        _type = type;
        _name = name;
        _rvalue = Optional.ofNullable(rvalue);
    }

    public Type getType() {
        return _type;
    }

    public Identifier getName() {
        return _name;
    }

    public Expression getRValue() {
        if (!hasInitializer()) {
            throw new RuntimeException("Variable declaration has no initializer");
        }

        return _rvalue.get();
    }

    public boolean hasInitializer() {
        return _rvalue.isPresent();
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
