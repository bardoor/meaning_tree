package org.vstu.meaningtree.utils.type_inference;

import org.jetbrains.annotations.NotNull;
import org.vstu.meaningtree.nodes.*;
import org.vstu.meaningtree.nodes.declarations.VariableDeclaration;
import org.vstu.meaningtree.nodes.declarations.components.VariableDeclarator;
import org.vstu.meaningtree.nodes.expressions.BinaryExpression;
import org.vstu.meaningtree.nodes.expressions.Literal;
import org.vstu.meaningtree.nodes.expressions.ParenthesizedExpression;
import org.vstu.meaningtree.nodes.expressions.UnaryExpression;
import org.vstu.meaningtree.nodes.expressions.bitwise.InversionOp;
import org.vstu.meaningtree.nodes.expressions.comparison.BinaryComparison;
import org.vstu.meaningtree.nodes.expressions.comparison.CompoundComparison;
import org.vstu.meaningtree.nodes.expressions.identifiers.SimpleIdentifier;
import org.vstu.meaningtree.nodes.expressions.literals.*;
import org.vstu.meaningtree.nodes.expressions.logical.*;
import org.vstu.meaningtree.nodes.expressions.math.AddOp;
import org.vstu.meaningtree.nodes.expressions.math.DivOp;
import org.vstu.meaningtree.nodes.expressions.other.AssignmentExpression;
import org.vstu.meaningtree.nodes.expressions.other.Range;
import org.vstu.meaningtree.nodes.expressions.other.TernaryOperator;
import org.vstu.meaningtree.nodes.expressions.unary.*;
import org.vstu.meaningtree.nodes.statements.CompoundStatement;
import org.vstu.meaningtree.nodes.statements.ExpressionStatement;
import org.vstu.meaningtree.nodes.statements.assignments.AssignmentStatement;
import org.vstu.meaningtree.nodes.statements.conditions.IfStatement;
import org.vstu.meaningtree.nodes.statements.conditions.SwitchStatement;
import org.vstu.meaningtree.nodes.types.UnknownType;
import org.vstu.meaningtree.nodes.types.builtin.*;

import java.util.ArrayList;
import java.util.List;

public class HindleyMilner {

    @NotNull
    public static NumericType inference(@NotNull NumericLiteral numericLiteral) {
        return switch (numericLiteral) {
            case FloatLiteral floatLiteral -> new FloatType();
            case IntegerLiteral integerLiteral -> new IntType();
            default -> throw new IllegalStateException("Unexpected number type: " + numericLiteral);
        };
    }

    @NotNull
    public static Type inference(@NotNull Literal literal) {
        return switch (literal) {
            case NumericLiteral numericLiteral -> inference(numericLiteral);
            case BoolLiteral boolLiteral -> new BooleanType();
            case StringLiteral stringLiteral -> new StringType();
            case InterpolatedStringLiteral interpolatedStringLiteral -> new StringType();
            case NullLiteral nullLiteral -> new UnknownType();
            default -> new UnknownType();
        };
    }

    @NotNull
    public static Type inference(
            @NotNull SimpleIdentifier identifier,
            @NotNull TypeScope scope) {
        Type inferredType = scope.getVariableType(identifier);
        if (inferredType == null) {
            return new UnknownType();
        }

        return inferredType;
    }

    @NotNull
    public static Type chooseGeneralType(Type first, Type second) {
        if (first instanceof NumericType && second instanceof NumericType) {
            if (first instanceof FloatType || second instanceof FloatType) {
                return new FloatType();
            }

            return new IntType();
        }
        else if (first instanceof StringType || second instanceof StringType) {
            return new StringType();
        }
        else if (first instanceof BooleanType && second instanceof BooleanType) {
            return new BooleanType();
        }

        return new UnknownType();
    }

    @NotNull
    public static Type chooseGeneralType(List<Type> types) {
        if (types.isEmpty()) {
            return new UnknownType();
        }
        else if (types.size() == 1) {
            return types.getFirst();
        }

        return chooseGeneralType(
                types.getFirst(),
                chooseGeneralType(types.subList(0, types.size() - 1))
        );
    }

    @NotNull
    private static List<Expression> expressionChildren(@NotNull Expression expression) {
        return expression
                .getChildren()
                .values()
                .stream()
                .map(node -> (Expression) node)
                .toList();
    }

    public static void backwardVariableTypeSet(
            @NotNull Expression expression,
            @NotNull TypeScope scope,
            @NotNull Type type) {
        List<Expression> children;
        if (expression instanceof SimpleIdentifier identifier) {
            children = List.of(identifier);
        }
        else {
            children = expressionChildren(expression);
        }

        for (Expression childExpression : children) {

            if (childExpression instanceof SimpleIdentifier identifier) {
                Type possibleType = scope.getVariableType(identifier);

                if (possibleType == null || possibleType instanceof UnknownType) {
                    scope.changeVariableType(identifier, type);
                }
                // Добавить что-то про обобщение типов
                else if (possibleType instanceof IntType
                        && type instanceof FloatType) {
                    scope.changeVariableType(identifier, type);
                }
            }
            else {
                backwardVariableTypeSet(childExpression, scope, type);
            }
        }
    }

    @NotNull
    public static Type inferenceOperandsTypeByExpressionType(@NotNull Expression expression) {
        return switch (expression) {
            case LongCircuitOrOp longCircuitOrOp -> new BooleanType();
            case LongCircuitAndOp longCircuitAndOp -> new BooleanType();
            case ShortCircuitOrOp shortCircuitOrOp -> new BooleanType();
            case ShortCircuitAndOp shortCircuitAndOp -> new BooleanType();
            case DivOp divOp -> new FloatType();
            default -> new UnknownType();
        };
    }

    @NotNull
    public static Type inference(@NotNull BinaryExpression binaryExpression, @NotNull TypeScope scope) {
        Expression left = binaryExpression.getLeft();
        Expression right = binaryExpression.getRight();

        Type leftType = inference(left, scope);
        Type rightType = inference(right, scope);

        if (leftType instanceof UnknownType && rightType instanceof UnknownType) {
            if (binaryExpression instanceof BinaryComparison) {
                leftType = rightType = new FloatType();
            }
            else {
                leftType = rightType = inferenceOperandsTypeByExpressionType(binaryExpression);
            }

            if (!(leftType instanceof UnknownType)) {
                backwardVariableTypeSet(left, scope, leftType);
                backwardVariableTypeSet(right, scope, rightType);
            }
        }
        else if (leftType instanceof UnknownType) {
            backwardVariableTypeSet(left, scope, rightType);
            leftType = rightType;
        }
        else if (rightType instanceof UnknownType) {
            backwardVariableTypeSet(left, scope, leftType);
            rightType = leftType;
        }

        if (binaryExpression instanceof BinaryComparison) {
            return new BooleanType();
        }
        else if (binaryExpression instanceof DivOp) {
            return new FloatType();
        }

        Type generalType = chooseGeneralType(leftType, rightType);
        if (left instanceof SimpleIdentifier leftIdentifier) {
            backwardVariableTypeSet(leftIdentifier, scope, generalType);
        }
        if (right instanceof SimpleIdentifier rightIdentifier) {
            backwardVariableTypeSet(rightIdentifier, scope, generalType);
        }

        return generalType;
    }

    @NotNull
    public static Type inference(@NotNull UnaryExpression unaryExpression, @NotNull TypeScope scope) {
        Expression argument = unaryExpression.getArgument();
        Type operandType = inference(argument, scope);

        if (unaryExpression instanceof NotOp) {
            // Тип операнда игнорируется?..
            Type expressionType = new BooleanType();
            backwardVariableTypeSet(argument, scope, expressionType);
            return expressionType;
        }
        else if (unaryExpression instanceof PostfixDecrementOp
                || unaryExpression instanceof PostfixIncrementOp
                || unaryExpression instanceof PrefixDecrementOp
                || unaryExpression instanceof PrefixIncrementOp
                || unaryExpression instanceof UnaryMinusOp
                || unaryExpression instanceof UnaryPlusOp
                || unaryExpression instanceof InversionOp
        ) {
            if (operandType instanceof UnknownType) {
                Type expressionType = new FloatType();
                backwardVariableTypeSet(argument, scope, expressionType);
                return expressionType;
            }
            return operandType;
        }

        throw new IllegalArgumentException("Unsupported unary expression type: " + unaryExpression.getClass());
    }

    @NotNull
    public static Type inference(@NotNull AssignmentExpression assignmentExpression, @NotNull TypeScope scope) {
        AddOp addOp = new AddOp(assignmentExpression.getLValue(), assignmentExpression.getRValue());
        return inference(addOp, scope);
    }

    @NotNull
    public static Type inference(@NotNull TernaryOperator ternaryOperator, @NotNull TypeScope scope) {
        inference(ternaryOperator.getCondition(), scope);
        Type thenExprType = inference(ternaryOperator.getThenExpr(), scope);
        Type elseExprType = inference(ternaryOperator.getElseExpr(), scope);
        return chooseGeneralType(thenExprType, elseExprType);
    }

    @NotNull
    public static Type inference(@NotNull Range range, @NotNull TypeScope scope) {
        var start = range.getStart();
        if (start != null) {
            inference(start, scope);
        }

        var stop = range.getStop();
        if (stop != null) {
            inference(range.getStop(), scope);
        }

        var step = range.getStep();
        if (step != null) {
            inference(range.getStep(), scope);
        }

        // Все, для чего нужен этот метод это обход детей диапазона,
        // чтобы вывести типы переменных, участвующих в формировании диапазона
        return new UnknownType();
    }

    @NotNull
    public static Type inference(@NotNull Expression expression, @NotNull TypeScope scope) {
        return switch (expression) {
            case Literal literal -> inference(literal);
            case SimpleIdentifier identifier -> inference(identifier, scope);
            case UnaryExpression unaryExpression -> inference(unaryExpression, scope);
            case BinaryExpression binaryExpression -> inference(binaryExpression, scope);
            case ParenthesizedExpression parenthesizedExpression -> inference(parenthesizedExpression.getExpression(), scope);
            case AssignmentExpression assignmentExpression -> inference(assignmentExpression, scope);
            case CompoundComparison compoundComparison -> inference(compoundComparison, scope);
            case TernaryOperator ternaryOperator -> inference(ternaryOperator, scope);
            case Range range -> inference(range, scope);
            default -> new UnknownType();
            //default -> throw new IllegalStateException("Unexpected expression type: " + expression.getClass());
        };
    }

    @NotNull
    public static Type inference(@NotNull Expression expression) {
        return inference(expression, new TypeScope());
    }

    public static void inference(@NotNull AssignmentStatement assignmentStatement, @NotNull TypeScope scope) {
        AssignmentExpression assignmentExpression = new AssignmentExpression(
                assignmentStatement.getLValue(),
                assignmentStatement.getRValue(),
                assignmentStatement.getAugmentedOperator()
        );
        inference(assignmentExpression, scope);
    }

    public static void inference(@NotNull CompoundStatement compoundStatement, @NotNull TypeScope scope) {

        for (var node : compoundStatement.getNodes()) {

            if (node instanceof Statement statement) {
                inference(statement, scope);
            }
            else if (node instanceof Expression expression) {
                inference(expression, scope);
            }
        }
    }

    public static void inference(@NotNull IfStatement ifStatement, @NotNull TypeScope scope) {

        for (var conditionBranch : ifStatement.getBranches()) {
            inference(conditionBranch, scope);
        }

        if (ifStatement.hasElseBranch()) {
            inference(ifStatement.getElseBranch(), scope);
        }
    }

    public static Type inference(@NotNull CompoundComparison compoundComparison, @NotNull TypeScope scope) {

        for (var binaryComparison : compoundComparison.getComparisons()) {
            inference(binaryComparison, scope);
        }

        return new BooleanType();
    }

    public static void inference(@NotNull SwitchStatement switchStatement, @NotNull TypeScope scope) {
        inference(switchStatement.getTargetExpression(), scope);
        for (var caseBranch : switchStatement.getCases()) {
            if (caseBranch != null) {
                inference(caseBranch, scope);
            }
        }
    }

    public static void inference(@NotNull Statement statement, @NotNull TypeScope scope) {
        inference(List.of(statement), scope);
    }

    public static void inference(@NotNull VariableDeclarator variableDeclarator, @NotNull TypeScope scope) {
        if (variableDeclarator.hasInitialization()) {
            inference(variableDeclarator.getRValue(), scope);
        }
        inference(variableDeclarator.getIdentifier(), scope);
    }

    public static void inference(@NotNull VariableDeclaration variableDeclaration, @NotNull TypeScope scope) {
        var types = new ArrayList<Type>();

        for (var variableDeclarator : variableDeclaration.getDeclarators()) {
            inference(variableDeclarator, scope);

            var varType = scope.getVariableType(variableDeclarator.getIdentifier());
            if (varType != null) {
                types.add(varType);
            }
            else if (variableDeclarator.hasInitialization()) {
                varType = inference(variableDeclarator.getRValue(), scope);
                types.add(varType);
            }
        }

        variableDeclaration.setType(chooseGeneralType(types));
    }
    
    public static void inference(@NotNull Declaration declaration, @NotNull TypeScope scope) {
        switch (declaration) {
            case VariableDeclaration variableDeclaration -> inference(variableDeclaration, scope);
            default -> throw new IllegalStateException("Unexpected declaration type: " + declaration.getClass());
        }
    }

    public static void inference(@NotNull List<Node> nodes, @NotNull TypeScope scope) {

        for (var node : nodes) {

            switch (node) {
                case ExpressionStatement expressionStatement -> inference(expressionStatement.getExpression(), scope);
                case AssignmentStatement assignmentStatement -> inference(assignmentStatement, scope);
                case CompoundStatement compoundStatement -> inference(compoundStatement, scope);
                case IfStatement ifStatement -> inference(ifStatement, scope);
                case SwitchStatement switchStatement -> inference(switchStatement, scope);
                case VariableDeclaration variableDeclaration -> inference(variableDeclaration, scope);
                case VariableDeclarator variableDeclarator -> inference(variableDeclarator, scope);
                case null, default -> {
                    List<Node> nodes_ = node
                            .getChildren()
                            .values()
                            .stream()
                            .map(obj -> (Node) obj)
                            .toList();

                    for (var node_ : nodes_) {

                        if (node_ instanceof Expression expression) {
                            inference(expression, scope);
                        } else if (node_ instanceof Statement s) {
                            inference(s, scope);
                        } else if (node_ instanceof Declaration d) {
                            inference(d, scope);
                        } else {
                            throw new IllegalArgumentException("Unsupported node type: " + node_.getClass());
                        }
                    }
                }
            }
        }
    }
}
