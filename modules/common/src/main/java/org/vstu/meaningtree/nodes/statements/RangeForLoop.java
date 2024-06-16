package org.vstu.meaningtree.nodes.statements;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Statement;
import org.vstu.meaningtree.nodes.identifiers.SimpleIdentifier;
import org.vstu.meaningtree.nodes.literals.IntegerLiteral;

/**
 * Цикл по диапазону целых чисел (начало и конец являются частью диапазна) с заданным шагом.
 */
public class RangeForLoop extends ForLoop {
    private final Expression _start;
    private final Expression _end;
    private final Expression _step;
    private final SimpleIdentifier _identifier;
    private Statement _body;

    /**
     * Создает цикл по диапазону.
     * @param start начало диапазона (включительно)
     * @param end конец диапазона (не включительно)
     * @param step _identifier
     * @param body тело цикла
     */
    public RangeForLoop(Expression start, Expression end, Expression step, SimpleIdentifier identifier, Statement body) {
        _start = start;
        _end = end;
        _step = step;
        _identifier = identifier;
        _body = body;
    }

    public Expression getStart() { return _start; }

    public Expression getEnd() { return _end; }

    public Expression getStep() { return _step; }

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

        builder.append(_start.generateDot());
        builder.append(_end.generateDot());
        builder.append(_step.generateDot());
        builder.append(_identifier.generateDot());
        builder.append(_body.generateDot());

        builder.append(String.format("%s -- %s [label=\"%s\"];\n", _id, _start.getId(), "start"));
        builder.append(String.format("%s -- %s [label=\"%s\"];\n", _id, _end.getId(), "end"));
        builder.append(String.format("%s -- %s [label=\"%s\"];\n", _id, _step.getId(), "step"));
        builder.append(String.format("%s -- %s [label=\"%s\"];\n", _id, _identifier.getId(), "ident"));
        builder.append(String.format("%s -- %s [label=\"%s\"];\n", _id, _body.getId(), "body"));

        return builder.toString();
    }

    public enum RANGE_TYPE {
        UP,
        DOWN,
        UNKNOWN,
    }

    public RANGE_TYPE getRangeType() {
        if (_start instanceof IntegerLiteral start
                && _end instanceof IntegerLiteral end
                && _step instanceof IntegerLiteral step) {
            int startValue = (int) start.getValue();
            int endValue = (int) end.getValue();
            int stepValue = (int) step.getValue();

            if (startValue < endValue && stepValue > 0) {
                return RANGE_TYPE.UP;
            }
            else if (startValue > endValue && stepValue < 0) {
                return RANGE_TYPE.DOWN;
            }
        }

        return RANGE_TYPE.UNKNOWN;
    }

    public int getStartValue() {
        if (_start instanceof IntegerLiteral start) {
            return (int) start.getValue();
        }

        throw new RuntimeException("Start value is not an integer");
    }

    public int getEndValue() {
        if (_end instanceof IntegerLiteral end) {
            return (int) end.getValue();
        }

        throw new RuntimeException("End value is not an integer");
    }

    public int getStepValue() {
        if (_step instanceof IntegerLiteral step) {
            return (int) step.getValue();
        }

        throw new RuntimeException("Step value is not an integer");
    }
}
