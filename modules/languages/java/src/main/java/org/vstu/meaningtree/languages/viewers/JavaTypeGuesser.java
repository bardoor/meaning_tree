package org.vstu.meaningtree.languages.viewers;

import org.vstu.meaningtree.nodes.*;
import org.vstu.meaningtree.nodes.comparison.BinaryComparison;
import org.vstu.meaningtree.nodes.declarations.VariableDeclaration;
import org.vstu.meaningtree.nodes.declarations.VariableDeclarator;
import org.vstu.meaningtree.nodes.literals.FloatLiteral;
import org.vstu.meaningtree.nodes.literals.IntegerLiteral;
import org.vstu.meaningtree.nodes.literals.StringLiteral;
import org.vstu.meaningtree.nodes.math.AddOp;
import org.vstu.meaningtree.nodes.types.*;

import java.util.ArrayList;
import java.util.List;

public class JavaTypeGuesser {

    public static Type guessType(VariableDeclaration variableDeclaration) {
        return guessType(variableDeclaration.getDeclarators());
    }

    public static Type guessType(VariableDeclarator... variableDeclarators) {
        if (variableDeclarators.length == 0) {
            throw new IllegalArgumentException("No declarators found");
        }

        List<Type> guessedTypes = new ArrayList<>();

        for (VariableDeclarator variableDeclarator : variableDeclarators) {
            variableDeclarator.getRValue().ifPresent(
                    expression -> guessedTypes.add(guessType(expression))
            );
        }

        // В случае, если хотя бы у одного выражения не получилось определить тип
        // в последовательности, то возвращаем неопределенный тип
        if (guessedTypes.stream().anyMatch(type -> type instanceof UnknownType)) {
            return new UnknownType();
        }

        Type guessedType = guessedTypes.getFirst();
        for (Type type : guessedTypes) {

            if (guessedType instanceof NumericType
                    && type instanceof NumericType) {

                if (guessedType instanceof IntType
                        && (type instanceof FloatType || type instanceof IntType)) {
                    guessedType = type;
                }
            }
            else if (guessedType instanceof StringType
                    && type instanceof StringType) {
                // Ничего не нужно делать: тип определен корректно
            }
            else {
                guessedType = new UnknownType();
                break;
            }
        }

        return guessedType;
    }

    public static Type guessType(Expression expression) {
        return switch (expression) {
            case UnaryExpression unaryExpression -> guessType(unaryExpression);
            case BinaryComparison binaryComparison -> guessType(binaryComparison);
            case BinaryExpression binaryExpression -> guessType(binaryExpression);
            case IntegerLiteral integerLiteral -> guessType(integerLiteral);
            case FloatLiteral floatLiteral -> guessType(floatLiteral);
            case StringLiteral stringLiteral -> guessType(stringLiteral);
            case TernaryOperator ternaryOperator -> guessType(ternaryOperator);
            case ParenthesizedExpression parenthesizedExpression -> guessType(parenthesizedExpression.getExpression());
            case CastTypeExpression castTypeExpression -> castTypeExpression.getCastType();
            default -> new UnknownType();
        };
    }

    private static Type guessType(TernaryOperator ternaryOperator) {
        // Для упрощения определения типа, достаточно преобразовать тернарник в
        // любой бинарный оператор, т.к. правила вывода типов будут одинаковы
        AddOp addOp = new AddOp(ternaryOperator.getThenExpr(), ternaryOperator.getElseExpr());
        return guessType(addOp);
    }

    private static Type guessType(StringLiteral stringLiteral) {
        return new StringType();
    }

    private static Type guessType(IntegerLiteral integerLiteral) {
        return new IntType();
    }

    private static Type guessType(FloatLiteral floatLiteral) {
        return new FloatType();
    }

    private static Type guessType(BinaryComparison binaryComparison) {
        return new BooleanType();
    }

    private static Type guessType(UnaryExpression unaryExpression) {
        return guessType(unaryExpression.getArgument());
    }

    public static Type guessType(BinaryExpression binaryExpression) {
        Type leftType = guessType(binaryExpression.getLeft());
        Type rightType = guessType(binaryExpression.getRight());

        if (leftType instanceof UnknownType
                || rightType instanceof UnknownType) {
            return new UnknownType();
        }
        else if (leftType instanceof FloatType
                || rightType instanceof FloatType) {
            return new FloatType();
        }
        else if (leftType instanceof IntType
                || rightType instanceof IntType) {
            return new IntType();
        }

        return new UnknownType();
    }
}
