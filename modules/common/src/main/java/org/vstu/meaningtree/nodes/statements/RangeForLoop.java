package org.vstu.meaningtree.nodes.statements;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Range;
import org.vstu.meaningtree.nodes.Statement;
import org.vstu.meaningtree.nodes.identifiers.SimpleIdentifier;
import org.vstu.meaningtree.nodes.literals.IntegerLiteral;

/**
 * Цикл по диапазону целых чисел (начало и конец являются частью диапазна) с заданным шагом.
 */
public class RangeForLoop extends ForLoop {
    private final Range _range;
    private final SimpleIdentifier _identifier;
    private Statement _body;

    /**
     * Создает цикл по диапазону.
     * @param range - выражение диапазона
     * @param identifier - идентификатор диапазона
     * @param body тело цикла
     */
    public RangeForLoop(Range range, SimpleIdentifier identifier, Statement body) {
        _range = range;
        _identifier = identifier;
        _body = body;
    }

    public Range getRange() { return _range; }

    public SimpleIdentifier getIdentifier() {
        return _identifier;
    }

    public Statement getBody() { return _body; }

    @Override
    public void makeBodyCompound() {
        if (!(_body instanceof CompoundStatement)) {
            _body = new CompoundStatement(_body);
        }
    }

    @Override
    public String generateDot() {
        StringBuilder builder = new StringBuilder();

        builder.append(String.format("%s [label=\"%s\"];\n", _id, getClass().getSimpleName()));

        builder.append(_range.generateDot());
        builder.append(_identifier.generateDot());
        builder.append(_body.generateDot());

        builder.append(String.format("%s -- %s [label=\"%s\"];\n", _id, _range.getId(), "range"));
        builder.append(String.format("%s -- %s [label=\"%s\"];\n", _id, _identifier.getId(), "ident"));
        builder.append(String.format("%s -- %s [label=\"%s\"];\n", _id, _body.getId(), "body"));

        return builder.toString();
    }


}
