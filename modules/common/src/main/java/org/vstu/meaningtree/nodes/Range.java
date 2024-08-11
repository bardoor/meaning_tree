package org.vstu.meaningtree.nodes;

import org.vstu.meaningtree.nodes.literals.IntegerLiteral;

import java.util.Objects;
import java.util.Optional;

public class Range extends Expression {
    private final Expression _start;
    private final Expression _stop;
    private final Expression _step;
    private final boolean _isExcludingStart;
    private final boolean _isExcludingEnd;

    public enum Type {
        UP,
        DOWN,
        UNKNOWN,
    }

    public Range(Expression start,
                 Expression stop,
                 Expression step,
                 boolean isExcludingStart,
                 boolean isExcludingEnd) {
        _start = start;
        _stop = stop;
        _step = step;
        _isExcludingStart = isExcludingStart;
        _isExcludingEnd = isExcludingEnd;
    }

    public Range(Expression start, Expression stop) {
        this(start, stop, null, false, true);
    }

    public static Range fromStart(Expression start) {
        return new Range(start, null);
    }

    public static Range untilStop(Expression stop) {
        return new Range(null, stop);
    }

    public Optional<Expression> getStart() {
        return Optional.ofNullable(_start);
    }

    public Optional<Expression> getStop() {
        return Optional.ofNullable(_stop);
    }

    public Optional<Expression> getStep() {
        return Optional.ofNullable(_step);
    }

    public boolean isExcludingStart() {
        return _isExcludingStart;
    }

    public boolean isExcludingEnd() {
        return _isExcludingEnd;
    }

    public Type getType() {
        try {
            long start = getStartValueAsLong();
            long stop = getStopValueAsLong();
            long step = getStepValueAsLong();

            if (start < stop && step > 0) {
                return Type.UP;
            }
            else if (start > stop && step < 0) {
                return Type.DOWN;
            }

            return Type.UNKNOWN;
        }
        catch (IllegalStateException exception) {
            return Type.UNKNOWN;
        }
    }

    public long getStartValueAsLong() throws IllegalStateException {
        if (_start == null) {
            throw new IllegalStateException("Start value is not specified");
        }

        if (!(_start instanceof IntegerLiteral)) {
            throw new IllegalStateException("Start value cannot be interpreted as long");
        }

        return ((IntegerLiteral) _start).getLongValue();
    }

    public long getStopValueAsLong() throws IllegalStateException {
        if (_stop == null) {
            throw new IllegalStateException("Stop value is not specified");
        }

        if (!(_stop instanceof IntegerLiteral)) {
            throw new IllegalStateException("Stop value cannot be interpreted as long");
        }

        return ((IntegerLiteral) _stop).getLongValue();
    }

    public long getStepValueAsLong() throws IllegalStateException {
        if (_step == null) {
            throw new IllegalStateException("Step value is not specified");
        }

        if (!(_step instanceof IntegerLiteral)) {
            throw new IllegalStateException("Step value cannot be interpreted as long");
        }

        return ((IntegerLiteral) _step).getLongValue();
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Range range = (Range) o;
        return Objects.equals(_start, range._start) && Objects.equals(_stop, range._stop) && Objects.equals(_step, range._step);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_start, _stop, _step);
    }
}
