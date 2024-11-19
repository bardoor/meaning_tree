package org.vstu.meaningtree.nodes.expressions.literals;

import org.jetbrains.annotations.NotNull;
import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.expressions.Literal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class InterpolatedStringLiteral extends Literal implements Iterable<Expression> {
    // Содержит подставляемые в строку выражения, а также статичные StringLiteral
    private final List<Expression> _components;
    private final StringLiteral.Type _type;

    public InterpolatedStringLiteral(StringLiteral.Type type, Expression ... components) {
        _type = type;
        _components = List.of(components);
    }

    public InterpolatedStringLiteral(StringLiteral.Type type, List<Expression> components) {
        _type = type;
        _components = new ArrayList<>(components);
    }

    public StringLiteral.Type getStringType() {
        return _type;
    }

    @NotNull
    @Override
    public Iterator<Expression> iterator() {
        return _components.iterator();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        InterpolatedStringLiteral that = (InterpolatedStringLiteral) o;
        return Objects.equals(_components, that._components) && _type == that._type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), _components, _type);
    }
}
