package org.vstu.meaningtree.nodes;

import org.vstu.meaningtree.nodes.literals.IntegerLiteral;

import java.util.Objects;
import java.util.Optional;

public class Range extends Expression {
    private final Optional<Expression> _start;
    private final Optional<Expression> _stop;
    private final Optional<Expression> _step;

    public enum Type {
        UP,
        DOWN,
        UNKNOWN,
    }

    public Range(Expression start, Expression stop, Expression step) {
        _start = Optional.ofNullable(start);
        _stop = Optional.ofNullable(stop);
        _step = Optional.ofNullable(step);
    }

    public boolean hasStart() {
        return _start.isPresent();
    }

    public boolean hasStop() {
        return _stop.isPresent();
    }

    public boolean hasStep() {
        return _step.isPresent();
    }

    public Expression getStart() {
        if (!hasStart()) {
            throw new RuntimeException("Start is not present");
        }
        return _start.get();
    }

    public Expression getStop() {
        if (!hasStop()) {
            throw new RuntimeException("Stop is not present");
        }
        return _stop.get();
    }

    public Expression getStep() {
        if (!hasStep()) {
            throw new RuntimeException("Step is not present");
        }
        return _step.get();
    }

    public boolean isStartIntegerValue() {
        return !hasStart() || (hasStart() && getStart() instanceof IntegerLiteral);
    }

    public boolean isStopIntegerValue() {
        return hasStop() && getStop() instanceof IntegerLiteral;
    }

    public boolean isStepIntegerValue() {
        return !hasStep() || (hasStep() && getStep() instanceof IntegerLiteral);
    }


    public Type getType() {
        if (isStartIntegerValue() && isStepIntegerValue() && isStopIntegerValue()) {
            int startValue = getStartIntegerValue();
            int endValue = getStopIntegerValue();
            int stepValue = getStepIntegerValue();

            if (startValue < endValue && stepValue > 0) {
                return Type.UP;
            }
            else if (startValue > endValue && stepValue < 0) {
                return Type.DOWN;
            }
        }

        return Type.UNKNOWN;
    }

    public int getStartIntegerValue() {
        if (!hasStart()) {
            return 0;
        }

        if (getStart() instanceof IntegerLiteral start) {
            return (int) start.getValue();
        }

        throw new RuntimeException("Start value is not an integer");
    }

    public int getStopIntegerValue() {
        if (getStop() instanceof IntegerLiteral end) {
            return (int) end.getValue();
        }

        throw new RuntimeException("End value is not an integer");
    }

    public int getStepIntegerValue() {
        if (!hasStep()) {
            return 1;
        }

        if (getStep() instanceof IntegerLiteral step) {
            return (int) step.getValue();
        }

        throw new RuntimeException("Step value is not an integer");
    }

    public Range(Expression start, Expression stop) {
        this(start, stop, null);
    }

    public static Range fromStart(Expression start) {
        return new Range(start, null);
    }

    public static Range untilStop(Expression stop) {
        return new Range(null, stop);
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
