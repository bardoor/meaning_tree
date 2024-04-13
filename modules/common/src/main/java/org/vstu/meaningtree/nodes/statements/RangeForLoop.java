package org.vstu.meaningtree.nodes.statements;

import org.vstu.meaningtree.nodes.identifiers.SimpleIdentifier;

/**
 * Цикл по диапазону целых чисел (начало и конец являются частью диапазна) с заданным шагом.
 */
public class RangeForLoop extends ForLoop {
    private final int _start;
    private final int _end;
    private final int _step;
    private final SimpleIdentifier _indentifier;
    private final CompoundStatement _body;

    /**
     * Создает цикл по диапазону.
     * @param start начало диапазона (включительно)
     * @param end конец диапазона (включительно)
     * @param step _identifier
     * @param body тело цикла
     */
    public RangeForLoop(int start, int end, int step, SimpleIdentifier identifier, CompoundStatement body) {
        _start = start;
        _end = end;
        _step = step;
        _indentifier = identifier;
        _body = body;
    }

    public int getStart() { return _start; }

    public int getEnd() { return _end; }

    public int getStep() { return _step; }

    public SimpleIdentifier getIdentifier() {
        return _indentifier;
    }

    public CompoundStatement getBody() { return _body; }

    @Override
    public String generateDot() {
        return String.format("%s [label=\"%s(var_name=\"%s\", start=%d, end=%d, step=%d)\"];\n",
                    _id, getClass().getSimpleName(), _indentifier.getName(), _start, _end, _step)
                + _body.generateDot()
                + String.format("%s -> %s;\n", _id, _body.getId());
    }
}
