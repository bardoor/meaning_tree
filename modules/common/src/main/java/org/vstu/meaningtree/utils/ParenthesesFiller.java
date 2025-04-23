package org.vstu.meaningtree.utils;

import org.apache.commons.lang3.tuple.Pair;
import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.expressions.BinaryExpression;
import org.vstu.meaningtree.nodes.expressions.ParenthesizedExpression;
import org.vstu.meaningtree.nodes.expressions.UnaryExpression;
import org.vstu.meaningtree.nodes.expressions.calls.FunctionCall;
import org.vstu.meaningtree.nodes.expressions.calls.MethodCall;
import org.vstu.meaningtree.nodes.expressions.identifiers.QualifiedIdentifier;
import org.vstu.meaningtree.nodes.expressions.other.CastTypeExpression;
import org.vstu.meaningtree.nodes.expressions.other.IndexExpression;
import org.vstu.meaningtree.nodes.expressions.other.MemberAccess;
import org.vstu.meaningtree.nodes.expressions.other.TernaryOperator;
import org.vstu.meaningtree.utils.tokens.OperatorArity;
import org.vstu.meaningtree.utils.tokens.OperatorAssociativity;
import org.vstu.meaningtree.utils.tokens.OperatorToken;

import java.util.function.Function;

public class ParenthesesFiller {
    // Warning: Приоритет в токенах от высшего (1) к низшему!!

    private Function<Expression, OperatorToken> _mapper;

    public ParenthesesFiller(Function<Expression, OperatorToken> mapperNodeToOperatorToken) {
        this._mapper = mapperNodeToOperatorToken;
    }

    public IndexExpression process(IndexExpression expr) {
        OperatorToken tok = _mapper.apply(expr);
        Expression arg = prepareUnary(tok, expr.getExpr());
        if (!arg.uniquenessEquals(expr.getExpr())) {
            expr = expr.clone();
            expr.substituteChildren("_expr", arg);
        }
        return expr;
    }

    public CastTypeExpression process(CastTypeExpression expr) {
        OperatorToken tok = _mapper.apply(expr);
        Expression arg = prepareUnary(tok, expr.getValue());
        if (!arg.uniquenessEquals(expr.getValue())) {
            expr = expr.clone();
            expr.substituteChildren("_value", arg);
        }
        return expr;
    }

    public MemberAccess process(MemberAccess expr) {
        OperatorToken tok = _mapper.apply(expr);
        Expression arg = prepareUnary(tok, expr.getExpression());
        if (!arg.uniquenessEquals(expr.getExpression())) {
            expr = expr.clone();
            expr.substituteChildren("_expr", arg);
        }
        return expr;
    }

    public FunctionCall process(FunctionCall expr) {
        if (expr.getFunction() == null || expr instanceof MethodCall) {
            return expr;
        }
        OperatorToken tok = _mapper.apply(expr);
        Expression arg = prepareUnary(tok, expr.getFunction());
        if (!arg.uniquenessEquals(expr.getFunction())) {
            expr = expr.clone();
            expr.substituteChildren("_function", arg);
        }
        return expr;
    }

    public TernaryOperator process(TernaryOperator expr) {
        OperatorToken tok = _mapper.apply(expr);
        Expression cond = prepareUnary(tok, expr.getCondition());
        Expression then = prepareUnary(tok, expr.getThenExpr());
        Expression elseBranch = prepareUnary(tok, expr.getElseExpr());
        expr = expr.clone();
        if (!expr.getCondition().uniquenessEquals(cond)) expr.substituteChildren("_condition", cond);
        if (!expr.getThenExpr().uniquenessEquals(then)) expr.substituteChildren("_thenExpr", then);
        if (!expr.getElseExpr().uniquenessEquals(elseBranch)) expr.substituteChildren("_elseExpr", elseBranch);
        return expr;
    }

    public BinaryExpression process(BinaryExpression expr) {
        OperatorToken tok = _mapper.apply(expr);
        if (tok == null) {
            return expr;
        }

        Pair<Expression, Expression> pair = prepareBinary(tok, expr.getLeft(), expr.getRight());
        if (!pair.getLeft().uniquenessEquals(expr.getLeft()) || !pair.getRight().uniquenessEquals(expr.getRight())) {
            expr = expr.clone();
            expr.substituteChildren("_left", pair.getLeft());
            expr.substituteChildren("_right", pair.getRight());
        }

        return expr;
    }

    public UnaryExpression process(UnaryExpression expr) {
        OperatorToken tok = _mapper.apply(expr);
        Expression arg = prepareUnary(tok, expr.getArgument());
        if (!arg.uniquenessEquals(expr.getArgument())) {
            expr = expr.clone();
            expr.substituteChildren("_argument", arg);
        }
        return expr;
    }

    public QualifiedIdentifier process(QualifiedIdentifier qual) {
        OperatorToken tok = _mapper.apply(qual);
        Expression arg = prepareUnary(tok, qual.getScope());
        if (!arg.uniquenessEquals(qual.getScope())) {
            qual = qual.clone();
            qual.substituteChildren("_scope", arg);
        }
        return qual;
    }

    private Expression prepareUnary(OperatorToken tok, Expression arg) {
        OperatorToken argTok = _mapper.apply(arg);
        if (tok == null || argTok == null) {
            return arg;
        }
        if (arg instanceof BinaryExpression || argTok.precedence > tok.precedence ||
                (arg instanceof UnaryExpression && tok.arity != OperatorArity.UNARY)) {
            arg = new ParenthesizedExpression(arg);
        }
        return arg;
    }

    private Pair<Expression, Expression> prepareBinary(OperatorToken tok, Expression left, Expression right) {
        OperatorToken tokLeft = _mapper.apply(left);
        OperatorToken tokRight = _mapper.apply(right);


        if (left instanceof BinaryExpression leftBinOp
                && tokLeft.precedence > tok.precedence) {
            left = new ParenthesizedExpression(leftBinOp);
        }

        if (tokRight != null && right instanceof BinaryExpression rightBinOp
                && tokRight.precedence > tok.precedence) {
            right = new ParenthesizedExpression(rightBinOp);
        }

        if (tokRight != null && right instanceof BinaryExpression rightBinOp) {
            if (tok.precedence == tokRight.precedence
                    && tok.assoc == tokRight.assoc && tok.assoc == OperatorAssociativity.LEFT
            ) {
                right = new ParenthesizedExpression(rightBinOp);
            }
        }

        if (tokLeft != null && left instanceof BinaryExpression leftBinOp) {
            if (tok.precedence == tokLeft.precedence
                    && tok.assoc == tokLeft.assoc && tok.assoc == OperatorAssociativity.RIGHT
            ) {
                left = new ParenthesizedExpression(leftBinOp);
            }
        }

        return Pair.of(left, right);
    }
}
