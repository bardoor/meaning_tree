package org.vstu.meaningtree.nodes.statements;

import org.vstu.meaningtree.nodes.Statement;

/**
 * Цикл по диапазону целых чисел (начало и конец являются частью диапазна) с заданным шагом.
 */
public class RangeForLoop extends Statement {
    private final int _start;
    private final int _end;
    private final int _step;
    private final CompoundStatement _body;

    /**
     * Создает цикл по диапазону.
     * @param start начало диапазона (включительно)
     * @param end конец диапазона (включительно)
     * @param step шаг
     * @param body тело цикла
     */
    public RangeForLoop(int start, int end, int step, CompoundStatement body) {
        _start = start;
        _end = end;
        _step = step;
        _body = body;
    }

    public int getStart() { return _start; }

    public int getEnd() { return _end; }

    public int getStep() { return _step; }

    public CompoundStatement getBody() { return _body; }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
