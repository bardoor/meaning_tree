package org.vstu.meaningtree.languages;

import org.jetbrains.annotations.NotNull;
import org.vstu.meaningtree.MeaningTree;
import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Node;
import org.vstu.meaningtree.nodes.Type;
import org.vstu.meaningtree.nodes.declarations.VariableDeclaration;
import org.vstu.meaningtree.nodes.declarations.components.VariableDeclarator;
import org.vstu.meaningtree.nodes.enums.AugmentedAssignmentOperator;
import org.vstu.meaningtree.nodes.expressions.BinaryExpression;
import org.vstu.meaningtree.nodes.expressions.Identifier;
import org.vstu.meaningtree.nodes.expressions.ParenthesizedExpression;
import org.vstu.meaningtree.nodes.expressions.UnaryExpression;
import org.vstu.meaningtree.nodes.expressions.bitwise.*;
import org.vstu.meaningtree.nodes.expressions.calls.FunctionCall;
import org.vstu.meaningtree.nodes.expressions.comparison.*;
import org.vstu.meaningtree.nodes.expressions.identifiers.SimpleIdentifier;
import org.vstu.meaningtree.nodes.expressions.literals.FloatLiteral;
import org.vstu.meaningtree.nodes.expressions.literals.IntegerLiteral;
import org.vstu.meaningtree.nodes.expressions.literals.NumericLiteral;
import org.vstu.meaningtree.nodes.expressions.logical.NotOp;
import org.vstu.meaningtree.nodes.expressions.logical.ShortCircuitAndOp;
import org.vstu.meaningtree.nodes.expressions.logical.ShortCircuitOrOp;
import org.vstu.meaningtree.nodes.expressions.math.AddOp;
import org.vstu.meaningtree.nodes.expressions.math.DivOp;
import org.vstu.meaningtree.nodes.expressions.math.MulOp;
import org.vstu.meaningtree.nodes.expressions.math.SubOp;
import org.vstu.meaningtree.nodes.expressions.other.AssignmentExpression;
import org.vstu.meaningtree.nodes.expressions.other.IndexExpression;
import org.vstu.meaningtree.nodes.expressions.other.TernaryOperator;
import org.vstu.meaningtree.nodes.expressions.unary.*;
import org.vstu.meaningtree.nodes.statements.ExpressionSequence;
import org.vstu.meaningtree.nodes.statements.ExpressionStatement;
import org.vstu.meaningtree.nodes.types.builtin.*;

import java.util.List;

import static org.vstu.meaningtree.nodes.enums.AugmentedAssignmentOperator.POW;

public class CppViewer extends LanguageViewer {

    @NotNull
    @Override
    public String toString(@NotNull MeaningTree meaningTree) {
        return toString(meaningTree.getRootNode());
    }

    @NotNull
    @Override
    public String toString(@NotNull Node node) {
        return switch (node) {
            case VariableDeclarator variableDeclarator -> toStringVariableDeclarator(variableDeclarator);
            case ExpressionStatement expressionStatement -> toStringExpressionStatement(expressionStatement);
            case VariableDeclaration variableDeclaration -> toStringVariableDeclaration(variableDeclaration);
            case IndexExpression indexExpression -> toStringIndexExpression(indexExpression);
            case ExpressionSequence commaExpression -> toStringCommaExpression(commaExpression);
            case TernaryOperator ternaryOperator -> toStringTernaryOperator(ternaryOperator);
            case FunctionCall functionCall -> toStringFunctionCall(functionCall);
            case ParenthesizedExpression parenthesizedExpression -> toStringParenthesizedExpression(parenthesizedExpression);
            case AssignmentExpression assignmentExpression -> toStringAssignmentExpression(assignmentExpression);
            case Identifier identifier -> toStringIdentifier(identifier);
            case Type type -> toStringType(type);
            case NumericLiteral numericLiteral -> toStringNumericLiteral(numericLiteral);
            case UnaryExpression unaryExpression -> toStringUnaryExpression(unaryExpression);
            case BinaryExpression binaryExpression -> toStringBinaryExpression(binaryExpression);
            default -> throw new IllegalStateException("Unexpected value: " + node);
        };
    }

    @NotNull
    private String toStringExpressionStatement(@NotNull ExpressionStatement expressionStatement) {
        return toString(expressionStatement.getExpression()) + ";";
    }

    @NotNull
    private String toStringVariableDeclarator(@NotNull VariableDeclarator variableDeclarator) {
        String variableName = toString(variableDeclarator.getIdentifier());

        Expression rValue = variableDeclarator.getRValue();
        if (rValue == null) {
            return variableName;
        }

        return "%s = %s".formatted(variableName, toString(rValue));
    }

    @NotNull
    private String toStringVariableDeclaration(@NotNull VariableDeclaration variableDeclaration) {
        StringBuilder builder = new StringBuilder();

        Type declarationType = variableDeclaration.getType();
        String type = toString(declarationType);
        builder
                .append(type)
                .append(" ");

        for (VariableDeclarator variableDeclarator : variableDeclaration.getDeclarators()) {
            builder.append(toString(variableDeclarator)).append(", ");
        }
        // Чтобы избежать лишней головной боли на проверки "а последняя ли это декларация",
        // я автоматически после каждой декларации добавляю запятую и пробел,
        // но для последней декларации они не нужны, поэтому эти два символа удаляются,
        // как сделать красивее - не знаю...
        builder.deleteCharAt(builder.length() - 1);
        builder.deleteCharAt(builder.length() - 1);

        builder.append(";");
        return builder.toString();
    }

    @NotNull
    private String toStringIndexExpression(@NotNull IndexExpression indexExpression) {
        String base = toString(indexExpression.getExpr());
        String indices = toString(indexExpression.getIndex());
        return "%s[%s]".formatted(base, indices);
    }

    @NotNull
    private String toStringCommaExpression(@NotNull ExpressionSequence commaExpression) {
        StringBuilder builder = new StringBuilder();

        for (Expression expression : commaExpression.getExpressions()) {
            builder
                    .append(toString(expression))
                    .append(", ");
        }

        if (builder.length() > 1) {
            builder.deleteCharAt(builder.length() - 1);
            builder.deleteCharAt(builder.length() - 1);
        }

        return builder.toString();
    }

    @NotNull
    private String toStringTernaryOperator(@NotNull TernaryOperator ternaryOperator) {
        String condition = toString(ternaryOperator.getCondition());
        String then = toString(ternaryOperator.getThenExpr());
        String else_ = toString(ternaryOperator.getElseExpr());
        return "%s ? %s : %s".formatted(condition, then, else_);
    }

    @NotNull
    private String toStringFunctionCallArgumentsList(@NotNull List<Expression> arguments) {
        StringBuilder builder = new StringBuilder();

        builder.append("(");

        for (Expression argument : arguments) {
            builder
                    .append(toString(argument))
                    .append(", ");
        }

        if (builder.length() > 1) {
            builder.deleteCharAt(builder.length() - 1);
            builder.deleteCharAt(builder.length() - 1);
        }

        builder.append(")");

        return builder.toString();
    }

    @NotNull
    private String toStringFunctionCall(@NotNull FunctionCall functionCall) {
        String functionName = toString(functionCall.getFunction());
        return functionName + toStringFunctionCallArgumentsList(functionCall.getArguments());
    }

    @NotNull
    private String toStringParenthesizedExpression(@NotNull ParenthesizedExpression parenthesizedExpression) {
        return "(" + toString(parenthesizedExpression.getExpression()) + ")";
    }

    @NotNull
    private String toStringAssignmentExpression(@NotNull AssignmentExpression assignmentExpression) {
        AugmentedAssignmentOperator op = assignmentExpression.getAugmentedOperator();
        String l = toString(assignmentExpression.getLValue());
        String r = toString(assignmentExpression.getRValue());

        // В С++ нет встроенного оператора возведения в степень, поэтому
        // используем функцию, необходимо убедится что подключен файл cmath: #include <cmath>
        if (op == POW) {
            return "%s = pow(%s, %s)".formatted(l, l, r);
        }

        String o = switch (op) {
            case NONE -> "=";
            case ADD -> "+=";
            case SUB -> "-=";
            case MUL -> "*=";
            // В C++ тип деления определяется не видом операции, а типом операндов,
            // поэтому один и тот же оператор
            case DIV, FLOOR_DIV -> "/=";
            case BITWISE_AND -> "&=";
            case BITWISE_OR -> "|=";
            case BITWISE_XOR -> "^=";
            case BITWISE_SHIFT_LEFT -> "<<=";
            case BITWISE_SHIFT_RIGHT -> ">>=";
            case MOD -> "%=";
            default -> throw new IllegalStateException("Unexpected type of augmented assignment operator: " + op);
        };

        if (assignmentExpression.getRValue() instanceof IntegerLiteral integerLiteral
                && (long) integerLiteral.getValue() == 1
                && (o.equals("+=") || o.equals("-="))) {
            o = switch (o) {
                case "+=" -> "++";
                case "-=" -> "--";
                default -> throw new IllegalArgumentException();
            };

            return l + o;
        }

        return "%s %s %s".formatted(l, o, r);
    }

    @NotNull
    private String toStringIdentifier(@NotNull Identifier identifier) {
        return switch (identifier) {
            case SimpleIdentifier simpleIdentifier -> simpleIdentifier.getName();
            default -> throw new IllegalStateException("Unexpected value: " + identifier);
        };
    }

    @NotNull
    private String toStringIntType(@NotNull IntType intType) {
        String prefix = intType.isUnsigned ? "unsigned" : "";
        
        String type = switch (intType.size) {
            case 8 -> "char";
            case 16 -> "short";
            case 32 -> "int";
            case 64 -> "long";
            default -> throw new IllegalStateException("Unexpected value: " + intType.size);
        };

        if (prefix.isEmpty()) {
            return type;
        }

        return prefix + " " + type;
    }

    @NotNull
    private String toStringFloatType(@NotNull FloatType floatType) {
        return switch (floatType.size) {
            case 32 -> "float";
            case 64 -> "double";
            default -> throw new IllegalStateException("Unexpected value: " + floatType.size);
        };
    }

    @NotNull
    private String toStringCharacterType(@NotNull CharacterType characterType) {
        return switch (characterType.size) {
            case 8 -> "char";
            case 16 -> "w_char";
            default -> throw new IllegalStateException("Unexpected value: " + characterType.size);
        };
    }

    @NotNull
    private String toStringType(@NotNull Type type) {
        return switch (type) {
            case IntType intType -> toStringIntType(intType);
            case FloatType floatType -> toStringFloatType(floatType);
            case CharacterType characterType -> toStringCharacterType(characterType);
            case BooleanType booleanType -> "bool";
            case VoidType voidType -> "void";
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
    }

    @NotNull
    private String toStringNumericLiteral(@NotNull NumericLiteral numericLiteral) {
        if (numericLiteral instanceof FloatLiteral floatLiteral) {
            String repr = floatLiteral.getStringValue();
            return floatLiteral.isDoublePrecision() ? repr : repr + "f";
        }

        IntegerLiteral integerLiteral = (IntegerLiteral) numericLiteral;
        return integerLiteral.getStringValue();
    }

    @NotNull
    private String toStringUnaryExpression(@NotNull UnaryExpression unaryExpression) {
        String operator = switch (unaryExpression) {
            case NotOp op -> "!";
            case InversionOp op -> "~";
            case UnaryMinusOp op -> "-";
            case UnaryPlusOp op -> "+";
            case PostfixIncrementOp op -> "++";
            case PrefixIncrementOp op -> "++";
            case PostfixDecrementOp op -> "--";
            case PrefixDecrementOp op -> "--";
            default -> throw new IllegalStateException("Unexpected value: " + unaryExpression);
        };

        if (unaryExpression instanceof PostfixDecrementOp
                || unaryExpression instanceof PostfixIncrementOp) {
            return toString(unaryExpression.getArgument()) + operator;
        }

        return operator + toString(unaryExpression.getArgument());
    }

    @NotNull
    private String toStringBinaryExpression(@NotNull BinaryExpression binaryExpression) {
        String operator = switch (binaryExpression) {
            case AddOp op -> "+";
            case SubOp op -> "-";
            case MulOp op -> "*";
            case DivOp op -> "/";
            case LtOp op -> "<";
            case GtOp op -> ">";
            case NotEqOp op -> "!=";
            case GeOp op -> ">=";
            case LeOp op -> "<=";
            case ShortCircuitAndOp op -> "&&";
            case ShortCircuitOrOp op -> "||";
            case BitwiseAndOp op -> "&";
            case BitwiseOrOp op -> "|";
            case XorOp op -> "^";
            case LeftShiftOp op -> "<<";
            case RightShiftOp op -> ">>";
            default -> throw new IllegalStateException("Unexpected value: " + binaryExpression);
        };

        return "%s %s %s".formatted(
                toString(binaryExpression.getLeft()),
                operator,
                toString(binaryExpression.getRight())
        );
    }
}
