package org.vstu.meaningtree.nodes.expressions.calls;

import org.vstu.meaningtree.exceptions.IllegalUsageException;
import org.vstu.meaningtree.exceptions.MeaningTreeException;
import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.expressions.ParenthesizedExpression;
import org.vstu.meaningtree.nodes.expressions.identifiers.SimpleIdentifier;
import org.vstu.meaningtree.nodes.expressions.other.MemberAccess;
import org.vstu.meaningtree.nodes.interfaces.Callable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FunctionCall extends Expression implements Callable {
    protected Expression _function;

    public Expression getFunction() {
        return _function;
    }

    public List<Expression> getArguments() {
        return List.copyOf(_arguments);
    }

    protected List<Expression> _arguments;

    public FunctionCall(Expression function, Expression ... arguments) {
        this(function, List.of(arguments));
    }

    public FunctionCall(Expression function, List<Expression> arguments) {
        if (function instanceof MemberAccess) {
            throw new IllegalUsageException("Use MethodCall instead this node");
        }
        this._function = function;
        this._arguments = arguments;
    }

    public boolean hasFunctionName() {
        return _function instanceof SimpleIdentifier || (_function instanceof ParenthesizedExpression paren && paren.getExpression() instanceof SimpleIdentifier);
    }

    public SimpleIdentifier getFunctionName() {
        if (hasFunctionName()) {
            if (_function instanceof ParenthesizedExpression paren) {
                return (SimpleIdentifier) paren.getExpression();
            }
            return (SimpleIdentifier) _function;
        }
        throw new MeaningTreeException("Function does not have identifier of call");
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        FunctionCall that = (FunctionCall) o;
        return Objects.equals(_function, that._function) && Objects.equals(_arguments, that._arguments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), _function, _arguments);
    }

    @Override
    public FunctionCall clone() {
        FunctionCall obj = (FunctionCall) super.clone();
        obj._function = _function.clone();
        obj._arguments = new ArrayList<>(_arguments.stream().map(Expression::clone).toList());
        return obj;
    }

    @Override
    public Expression getCallableName() {
        return _function;
    }
}
