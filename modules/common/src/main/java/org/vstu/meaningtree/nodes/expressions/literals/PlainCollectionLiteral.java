package org.vstu.meaningtree.nodes.expressions.literals;

import org.jetbrains.annotations.Nullable;
import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Type;
import org.vstu.meaningtree.nodes.expressions.newexpr.ArrayNewExpression;
import org.vstu.meaningtree.nodes.expressions.other.ArrayInitializer;
import org.vstu.meaningtree.nodes.types.UnknownType;
import org.vstu.meaningtree.nodes.types.containers.components.Shape;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class PlainCollectionLiteral extends CollectionLiteral {
    private List<Expression> _content;
    private Type typeHint = new UnknownType();

    public PlainCollectionLiteral(Expression ... content) {
        _content = List.of(content);
    }

    public void setTypeHint(Type type) {
        if (type != null) this.typeHint = type;
    }

    @Nullable
    public Type getTypeHint() {
        return typeHint;
    }

    public PlainCollectionLiteral(List<Expression> exprs) {_content = new ArrayList<>(exprs);}

    public List<Expression> getList() {
        return List.copyOf(_content);
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlainCollectionLiteral that = (PlainCollectionLiteral) o;
        return Objects.equals(_content, that._content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), _content);
    }

    @Override
    public PlainCollectionLiteral clone() {
        PlainCollectionLiteral obj = (PlainCollectionLiteral) super.clone();
        if (typeHint != null) obj.typeHint = typeHint.clone();
        obj._content = new ArrayList<>(_content.stream().map(Expression::clone).toList());
        return obj;
    }

    public ArrayNewExpression toArrayNew() {
        int dimensions = 1;
        Expression item = getList().getFirst();
        while (item instanceof PlainCollectionLiteral || (item instanceof ArrayNewExpression arr && typeHint.equals(arr.getType()))) {
            if (item instanceof PlainCollectionLiteral list)
            {
                item = list.getList().getFirst();
                dimensions++;
            }
            if (item instanceof ArrayNewExpression arr) dimensions += arr.getShape().getDimensionCount();
        }
        return new ArrayNewExpression(getTypeHint(), new Shape(dimensions), new ArrayInitializer(getList()));
    }
}
