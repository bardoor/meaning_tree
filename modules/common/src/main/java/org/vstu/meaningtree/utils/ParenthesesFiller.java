package org.vstu.meaningtree.utils;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.expressions.BinaryExpression;
import org.vstu.meaningtree.nodes.expressions.ParenthesizedExpression;
import org.vstu.meaningtree.nodes.expressions.UnaryExpression;
import org.vstu.meaningtree.utils.tokens.OperatorAssociativity;
import org.vstu.meaningtree.utils.tokens.OperatorToken;

import java.util.function.Function;

public class ParenthesesFiller {
    private Function<Expression, OperatorToken> _mapper;

    public ParenthesesFiller(Function<Expression, OperatorToken> mapperNodeToOperatorToken) {
        this._mapper = mapperNodeToOperatorToken;
    }

    public BinaryExpression makeNewExpression(BinaryExpression expr) {
        expr = expr.clone();

        OperatorToken tok = _mapper.apply(expr);

        Expression left = expr.getLeft();
        OperatorToken tokLeft = _mapper.apply(expr.getLeft());

        Expression right = expr.getRight();
        OperatorToken tokRight = _mapper.apply(expr.getRight());

        if (left instanceof BinaryExpression leftBinOp
                && tokLeft.precedence > tok.precedence) {
            left = new ParenthesizedExpression(leftBinOp);
        }

        if (right instanceof BinaryExpression rightBinOp
                && tokRight.precedence > tok.precedence) {
            right = new ParenthesizedExpression(rightBinOp);
        }

        if (right instanceof BinaryExpression rightBinOp) {
            if (tok.precedence == tokRight.precedence
                    && tok.assoc == tokRight.assoc && tok.assoc == OperatorAssociativity.LEFT
            ) {
                right = new ParenthesizedExpression(rightBinOp);
            }
        }

        if (left instanceof BinaryExpression leftBinOp) {
            if (tok.precedence == tokLeft.precedence
                    && tok.assoc == tokLeft.assoc && tok.assoc == OperatorAssociativity.RIGHT
            ) {
                left = new ParenthesizedExpression(leftBinOp);
            }
        }

        expr.substituteChildren("_left", left);
        expr.substituteChildren("_right", right);
        return expr;
    }

    public UnaryExpression makeNewExpression(UnaryExpression expr) {
        expr = expr.clone();
        Expression arg = expr.getArgument();
        OperatorToken tok = _mapper.apply(expr);
        OperatorToken argTok = _mapper.apply(arg);

        if (arg instanceof BinaryExpression ||
                (arg instanceof UnaryExpression && argTok.precedence < tok.precedence)
        ) {
            arg = new ParenthesizedExpression(arg);
        }
        expr.substituteChildren("_argument", arg);
        return expr;
    }
}
