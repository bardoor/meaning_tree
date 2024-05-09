package org.vstu.meaningtree.nodes.statements;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Statement;
import org.vstu.meaningtree.nodes.identifiers.SimpleIdentifier;

/**
 * Цикл по диапазону целых чисел (начало и конец являются частью диапазна) с заданным шагом.
 */
public class RangeForLoop extends ForLoop {
    private final Expression _start;
    private final Expression _end;
    private final Expression _step;
    private final SimpleIdentifier _indentifier;
    private final Statement _body;

    /**
     * Создает цикл по диапазону.
     * @param start начало диапазона (включительно)
     * @param end конец диапазона (включительно)
     * @param step _identifier
     * @param body тело цикла
     */
    public RangeForLoop(Expression start, Expression end, Expression step, SimpleIdentifier identifier, Statement body) {
        _start = start;
        _end = end;
        _step = step;
        _indentifier = identifier;
        _body = body;
    }

    public Expression getStart() { return _start; }

    public Expression getEnd() { return _end; }

    public Expression getStep() { return _step; }

    public SimpleIdentifier getIdentifier() {
        return _indentifier;
    }

    public Statement getBody() { return _body; }

    @Override
    public String generateDot() {
        //TODO: fix for new format
        return String.format("%s [label=\"%s(var_name=\"%s\", start=%d, end=%d, step=%d)\"];\n",
                    _id, getClass().getSimpleName(), _indentifier.getName(), _start, _end, _step)
                + _body.generateDot()
                + String.format("%s -> %s;\n", _id, _body.getId());
    }
}
