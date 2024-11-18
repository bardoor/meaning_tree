package org.vstu.meaningtree.nodes.expressions.other;

import org.jetbrains.annotations.Nullable;
import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.expressions.literals.IntegerLiteral;

import java.util.Objects;

public class Range extends Expression {
    @Nullable
    private final Expression _start;

    @Nullable
    private final Expression _stop;

    @Nullable
    private final Expression _step;

    private final boolean _isExcludingStart;
    private final boolean _isExcludingEnd;

    private Range.Type _rangeType;

    public enum Type {
        UP,
        DOWN,
        UNKNOWN,
    }

    public Range(@Nullable Expression start,
                 @Nullable Expression stop,
                 @Nullable Expression step,
                 boolean isExcludingStart,
                 boolean isExcludingEnd,
                 Range.Type rangeType
    ) {
        _start = start;
        _stop = stop;
        _step = step;
        _isExcludingStart = isExcludingStart;
        _isExcludingEnd = isExcludingEnd;
        _rangeType = rangeType;
    }

    public Range(Expression start, Expression stop) {
        this(start, stop, null, false, true, Type.UNKNOWN);
    }

    public static Range fromStart(Expression start) {
        return new Range(start, null);
    }

    public static Range untilStop(Expression stop) {
        return new Range(null, stop);
    }

    @Nullable
    public Expression getStart() {
        return _start;
    }

    @Nullable
    public Expression getStop() {
        return _stop;
    }

    @Nullable
    public Expression getStep() {
        return _step;
    }

    public boolean isExcludingStart() {
        return _isExcludingStart;
    }

    public boolean isExcludingEnd() {
        return _isExcludingEnd;
    }

    public Type getType() {
        if (_rangeType != Type.UNKNOWN) {
            return _rangeType;
        }

        try {
            long start = getStartValueAsLong();
            long stop = getStopValueAsLong();
            long step = getStepValueAsLong();

            if (start < stop && step > 0) {
                _rangeType = Type.UP;
            }
            else if (start > stop && step < 0) {
                _rangeType = Type.DOWN;
            }
            else {
                _rangeType = Type.UNKNOWN;
            }
        }
        catch (IllegalStateException exception) {
            _rangeType = Type.UNKNOWN;
        }

        return _rangeType;
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
        return Objects.hash(super.hashCode(), _start, _stop, _step);
    }
}
