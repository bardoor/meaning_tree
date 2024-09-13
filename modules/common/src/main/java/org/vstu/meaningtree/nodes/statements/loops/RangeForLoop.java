package org.vstu.meaningtree.nodes.statements.loops;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.expressions.other.Range;
import org.vstu.meaningtree.nodes.Statement;
import org.vstu.meaningtree.nodes.expressions.identifiers.SimpleIdentifier;
import org.vstu.meaningtree.nodes.statements.CompoundStatement;

/**
 * Цикл по диапазону целых чисел (начало и конец являются частью диапазона) с заданным шагом.
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

    /**
     * Создает цикл по диапазону.
     * @param start начало диапазона (включительно)
     * @param end конец диапазона (не включительно)
     * @param step _identifier
     * @param body тело цикла
     */
    public RangeForLoop(Expression start,
                        Expression end,
                        Expression step,
                        boolean isExcludingStart,
                        boolean isExcludingEnd,
                        SimpleIdentifier identifier,
                        Statement body) {
        this(new Range(start, end, step, isExcludingStart, isExcludingEnd, Range.Type.UNKNOWN), identifier, body);
    }

    public Range getRange() {
        return _range;
    }

    public SimpleIdentifier getIdentifier() {
        return _identifier;
    }

    public Statement getBody() { return _body; }

    public Range.Type getRangeType() {
        return _range.getType();
    }

    public Expression getStart() {
        return _range.getStart();
    }

    public Expression getStop() {
        return _range.getStop();
    }

    public Expression getStep() {
        return _range.getStep();
    }

    public long getStartValueAsLong() throws IllegalStateException {
        return _range.getStartValueAsLong();
    }

    public long getStopValueAsLong() throws IllegalStateException {
        return _range.getStopValueAsLong();
    }

    public long getStepValueAsLong() throws IllegalStateException {
        return _range.getStepValueAsLong();
    }

    public boolean isExcludingStop() {
        return _range.isExcludingEnd();
    }

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
