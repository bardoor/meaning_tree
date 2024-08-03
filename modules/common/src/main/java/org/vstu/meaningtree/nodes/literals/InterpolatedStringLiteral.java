package org.vstu.meaningtree.nodes.literals;

import org.vstu.meaningtree.nodes.Expression;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

    @Override
    public Iterator<Expression> iterator() {
        return _components.iterator();
    }
}
