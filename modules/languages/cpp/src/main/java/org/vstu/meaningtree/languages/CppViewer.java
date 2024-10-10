package org.vstu.meaningtree.languages;

import org.jetbrains.annotations.NotNull;
import org.vstu.meaningtree.MeaningTree;
import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Node;
import org.vstu.meaningtree.nodes.ProgramEntryPoint;
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
import org.vstu.meaningtree.nodes.expressions.identifiers.QualifiedIdentifier;
import org.vstu.meaningtree.nodes.expressions.identifiers.ScopedIdentifier;
import org.vstu.meaningtree.nodes.expressions.identifiers.SimpleIdentifier;
import org.vstu.meaningtree.nodes.expressions.literals.*;
import org.vstu.meaningtree.nodes.expressions.logical.NotOp;
import org.vstu.meaningtree.nodes.expressions.logical.ShortCircuitAndOp;
import org.vstu.meaningtree.nodes.expressions.logical.ShortCircuitOrOp;
import org.vstu.meaningtree.nodes.expressions.math.*;
import org.vstu.meaningtree.nodes.expressions.newexpr.ArrayNewExpression;
import org.vstu.meaningtree.nodes.expressions.newexpr.NewExpression;
import org.vstu.meaningtree.nodes.expressions.newexpr.ObjectNewExpression;
import org.vstu.meaningtree.nodes.expressions.newexpr.PlacementNewExpression;
import org.vstu.meaningtree.nodes.expressions.other.*;
import org.vstu.meaningtree.nodes.expressions.pointers.PointerMemberAccess;
import org.vstu.meaningtree.nodes.expressions.pointers.PointerPackOp;
import org.vstu.meaningtree.nodes.expressions.pointers.PointerUnpackOp;
import org.vstu.meaningtree.nodes.expressions.unary.*;
import org.vstu.meaningtree.nodes.statements.DeleteStatement;
import org.vstu.meaningtree.nodes.statements.ExpressionSequence;
import org.vstu.meaningtree.nodes.statements.ExpressionStatement;
import org.vstu.meaningtree.nodes.statements.assignments.AssignmentStatement;
import org.vstu.meaningtree.nodes.types.GenericUserType;
import org.vstu.meaningtree.nodes.types.NoReturn;
import org.vstu.meaningtree.nodes.types.UnknownType;
import org.vstu.meaningtree.nodes.types.UserType;
import org.vstu.meaningtree.nodes.types.builtin.*;
import org.vstu.meaningtree.nodes.types.containers.ArrayType;
import org.vstu.meaningtree.nodes.types.containers.DictionaryType;
import org.vstu.meaningtree.nodes.types.containers.ListType;
import org.vstu.meaningtree.nodes.types.containers.SetType;

import java.util.ArrayList;
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
            case ProgramEntryPoint entryPoint -> toStringEntryPoint(entryPoint);
            case VariableDeclarator variableDeclarator -> toStringVariableDeclarator(variableDeclarator);
            case ExpressionStatement expressionStatement -> toStringExpressionStatement(expressionStatement);
            case VariableDeclaration variableDeclaration -> toStringVariableDeclaration(variableDeclaration);
            case IndexExpression indexExpression -> toStringIndexExpression(indexExpression);
            case ExpressionSequence commaExpression -> toStringCommaExpression(commaExpression);
            case TernaryOperator ternaryOperator -> toStringTernaryOperator(ternaryOperator);
            case FunctionCall functionCall -> toStringFunctionCall(functionCall);
            case ParenthesizedExpression parenthesizedExpression -> toStringParenthesizedExpression(parenthesizedExpression);
            case AssignmentExpression assignmentExpression -> toStringAssignmentExpression(assignmentExpression);
            case AssignmentStatement assignmentStatement -> toStringAssignmentExpression(assignmentStatement.toExpression()).concat(";");
            case Type type -> toStringType(type);
            case Identifier identifier -> toStringIdentifier(identifier);
            case NumericLiteral numericLiteral -> toStringNumericLiteral(numericLiteral);
            case FloorDivOp floorDivOp -> toStringFloorDiv(floorDivOp);
            case UnaryExpression unaryExpression -> toStringUnaryExpression(unaryExpression);
            case BinaryExpression binaryExpression -> toStringBinaryExpression(binaryExpression);
            case NullLiteral nullLit -> "NULL";
            case StringLiteral sl -> toStringStringLiteral(sl);
            case BoolLiteral bl -> bl.getValue() ? "true" : "false";
            case PlainCollectionLiteral colLit -> toStringCollectionLiteral(colLit);
            case CastTypeExpression cast -> toStringCast(cast);
            case SizeofExpression sizeof -> toStringSizeof(sizeof);
            case NewExpression new_ -> toStringNew(new_);
            case DeleteExpression del -> toStringDelete(del);
            case DeleteStatement del -> toStringDelete(del.toExpression()) + ";";
            case MemberAccess memAccess -> toStringMemberAccess(memAccess);
            case CompoundComparison cmpCmp -> toStringCompoundComparison(cmpCmp);
            case InterpolatedStringLiteral interpolatedStringLiteral -> fromInterpolatedString(interpolatedStringLiteral);
            default -> throw new IllegalStateException("Unexpected value: " + node);
        };
    }

    private String toStringCompoundComparison(CompoundComparison cmpCmp) {
        StringBuilder expr = new StringBuilder();
        for (BinaryComparison cmp : cmpCmp.getComparisons()) {
            expr.append(toStringBinaryExpression(cmp));
            expr.append(" && ");
        }
        return expr.substring(0, expr.length() - 4);
    }

    private String toStringMemberAccess(MemberAccess memAccess) {
        String token = memAccess instanceof PointerMemberAccess ? "->" : ".";
        return String.format("%s%s%s",toString(memAccess.getExpression()), token, toString(memAccess.getMember()));
    }

    private String fromInterpolatedString(InterpolatedStringLiteral interpolatedStringLiteral) {
        StringBuilder builder = new StringBuilder("std::format(\"");
        List<Expression> dynamicExprs = new ArrayList<>();
        for (Expression expr : interpolatedStringLiteral) {
            if (expr instanceof StringLiteral str) {
                builder.append(str.getEscapedValue());
            } else {
                builder.append("{}");
                dynamicExprs.add(expr);
            }
        }
        builder.append('\"');
        if (!dynamicExprs.isEmpty()) {
            builder.append(", ");
            builder.append(toStringArguments(dynamicExprs));
        }
        builder.append(")");
        return builder.toString();
    }

    private String toStringDelete(DeleteExpression del) {
        StringBuilder builder = new StringBuilder("delete");
        if (del.isCollectionTarget()) {
            builder.append("[]");
        }
        builder.append(' ');
        builder.append(toString(del.getTarget()));
        return builder.toString();
    }

    private String toStringNew(NewExpression _new) {
        if (_new instanceof ArrayNewExpression arrayNew) {
            StringBuilder newBuilder = new StringBuilder();
            // DISABLED DUE TO RARE SYNTAX
            /*
            StringBuilder newBuilder = new StringBuilder("new ");
            newBuilder.append(toString(arrayNew.getType()));
            for (int i = 0; i < arrayNew.getShape().getDimensionCount(); i++) {
                newBuilder.append(String.format("[%s]", arrayNew.getShape().getDimension(i)));
            }
            */
            if (arrayNew.getInitializer() != null) {
                // newBuilder.append(' ');
                newBuilder.append(String.format("{%s}", toStringArguments(arrayNew.getInitializer().getValues())));
            } else {
                newBuilder.append("new ");
                newBuilder.append(toString(arrayNew.getType()));
                for (int i = 0; i < arrayNew.getShape().getDimensionCount(); i++) {
                    newBuilder.append(String.format("[%s]", arrayNew.getShape().getDimension(i)));
                }
            }
            return newBuilder.toString();
        } else if (_new instanceof PlacementNewExpression placementNew) {
            return String.format("new(%s) %s", toStringArguments(placementNew.getConstructorArguments()), toString(placementNew.getType()));
        } else if (_new instanceof ObjectNewExpression objectNew) {
            return String.format("new %s(%s)", toString(objectNew.getType()), toStringArguments(objectNew.getConstructorArguments()));
        } else {
            throw new RuntimeException("Unknown new expression");
        }
    }

    private String toStringSizeof(SizeofExpression sizeof) {
        return String.format("sizeof(%s)", toString(sizeof.getExpression()));
    }

    private String toStringCast(CastTypeExpression cast) {
        return String.format("(%s)%s", toString(cast.getCastType()), toString(cast.getValue()));
    }

    private String toStringCollectionLiteral(PlainCollectionLiteral colLit) {
        return String.format("{%s}", toStringArguments(colLit.getList()));
    }

    private String toStringArguments(List<Expression> exprs) {
        return String.join(", ", exprs.stream().map(this::toString).toList());
    }

    private String toStringStringLiteral(StringLiteral literal) {
        return String.format("\"%s\"", literal.getEscapedValue());
    }

    private String toStringFloorDiv(FloorDivOp op) {
        return String.format("(long) (%s / %s)", toString(op.getLeft()), toString(op.getRight()));
    }

    private String toStringEntryPoint(ProgramEntryPoint entryPoint) {
        // TODO: required main function creation or expression mode
        StringBuilder builder = new StringBuilder();
        for (Node node : entryPoint.getBody()) {
            builder.append(toString(node));
            builder.append("\n");
        }
        return builder.toString();
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
            case ScopedIdentifier scopedIdentifier -> String.join(".", scopedIdentifier.getScopeResolution().stream().map(this::toStringIdentifier).toList());
            case QualifiedIdentifier qualifiedIdentifier -> String.format("%s::%s", this.toStringIdentifier(qualifiedIdentifier.getScope()), this.toStringIdentifier(qualifiedIdentifier.getMember()));
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
            case 16 -> "char16_t";
            default -> throw new IllegalStateException("Unexpected value: " + characterType.size);
        };
    }

    @NotNull
    private String toStringType(@NotNull Type type) {
        String initialType = switch (type) {
            case IntType intType -> toStringIntType(intType);
            case FloatType floatType -> toStringFloatType(floatType);
            case CharacterType characterType -> toStringCharacterType(characterType);
            case BooleanType booleanType -> "bool";
            case NoReturn voidType -> "void";
            case UnknownType unknown -> "auto";
            case PointerType ptr -> {
                if (ptr.getTargetType() instanceof UnknownType) {
                    yield "void *";
                }
                if (type.isConst()) {
                    yield String.format("%s * const", toStringType(ptr.getTargetType()));
                }
                yield String.format("%s *", toStringType(ptr.getTargetType()));
            }
            case ReferenceType ref ->  {
                if (type.isConst()) {
                    yield String.format("%s & const", toStringType(ref.getTargetType()));
                }
                yield String.format("%s &", toStringType(ref.getTargetType()));
            }
            case DictionaryType dct -> String.format("std::map<%s, %s>", toStringType(dct.getKeyType()), toStringType(dct.getValueType()));
            case ListType lst -> String.format("std::list<%s>", toStringType(lst.getItemType()));
            case ArrayType array ->  String.format("std::array<%s>", toStringType(array.getItemType()));
            case SetType set ->  String.format("std::set<%s>", toStringType(set.getItemType()));
            case StringType str -> "std::string"; // TODO: пока нет способа хорошо представить юникод-строки
            case GenericUserType gusr -> String.format("%s<%s>", toString(gusr.getQualifiedName()), toStringArguments(List.of(gusr.getTypeParameters())));
            case UserType usr -> toString(usr.getQualifiedName());
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
        if (type.isConst() && !(type instanceof ReferenceType) && !(type instanceof PointerType)) {
            return "const ".concat(initialType);
        }
        return initialType;
    }

    @NotNull
    private String toStringNumericLiteral(@NotNull NumericLiteral numericLiteral) {
        if (numericLiteral instanceof FloatLiteral floatLiteral) {
            return floatLiteral.getStringValue(true);
        }

        IntegerLiteral integerLiteral = (IntegerLiteral) numericLiteral;
        String result = integerLiteral.getStringValue(false);
        if (integerLiteral.isUnsigned()) {
            result = result.concat("U");
        }
        if (integerLiteral.isLong()) {
            result = result.concat("L");
        }
        return result;
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
            case PointerPackOp op -> "&";
            case PointerUnpackOp op -> "*";
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
        if (binaryExpression instanceof PowOp) {
            return String.format("pow(%s, %s)", toString(binaryExpression.getLeft()), toString(binaryExpression.getRight()));
        }
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
            case EqOp op -> "==";
            case ModOp op -> "%";
            case ThreeWayComparisonOp op -> "<=>";
            default -> throw new IllegalStateException("Unexpected value: " + binaryExpression);
        };

        return "%s %s %s".formatted(
                toString(binaryExpression.getLeft()),
                operator,
                toString(binaryExpression.getRight())
        );
    }
}
