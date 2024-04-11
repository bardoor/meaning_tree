package org.vstu.meaningtree.nodes;

import java.util.Optional;

public class Range extends Expression {
    private final Optional<Expression> _start;
    private final Optional<Expression> _stop;
    private final Optional<Expression> _step;

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
}
