package org.vstu.meaningtree.nodes.expressions.literals;

import org.jetbrains.annotations.NotNull;
import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.expressions.Literal;
import org.vstu.meaningtree.utils.TreeNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class InterpolatedStringLiteral extends Literal implements Iterable<Expression> {
    // Содержит подставляемые в строку выражения, а также статичные StringLiteral
    @TreeNode private List<Expression> components;
    private StringLiteral.Type type;

    public InterpolatedStringLiteral(StringLiteral.Type type, Expression ... components) {
        this.type = type;
        this.components = List.of(components);
    }

    public InterpolatedStringLiteral(StringLiteral.Type type, List<Expression> components) {
        this.type = type;
        this.components = new ArrayList<>(components);
    }

    public StringLiteral.Type getStringType() {
        return type;
    }

    public List<Expression> components() {return components;}

    @NotNull
    @Override
    public Iterator<Expression> iterator() {
        return components.iterator();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        InterpolatedStringLiteral that = (InterpolatedStringLiteral) o;
        return Objects.equals(components, that.components) && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), components, type);
    }
}
