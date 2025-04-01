package org.vstu.meaningtree.languages;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import jakarta.json.Json;
import org.jetbrains.annotations.NotNull;
import org.vstu.meaningtree.nodes.Comment;
import org.vstu.meaningtree.nodes.Node;
import org.vstu.meaningtree.nodes.ProgramEntryPoint;
import org.vstu.meaningtree.nodes.declarations.VariableDeclaration;
import org.vstu.meaningtree.nodes.expressions.ParenthesizedExpression;
import org.vstu.meaningtree.nodes.expressions.bitwise.*;
import org.vstu.meaningtree.nodes.expressions.calls.FunctionCall;
import org.vstu.meaningtree.nodes.expressions.comparison.*;
import org.vstu.meaningtree.nodes.expressions.identifiers.SimpleIdentifier;
import org.vstu.meaningtree.nodes.expressions.literals.*;
import org.vstu.meaningtree.nodes.expressions.logical.NotOp;
import org.vstu.meaningtree.nodes.expressions.logical.ShortCircuitAndOp;
import org.vstu.meaningtree.nodes.expressions.logical.ShortCircuitOrOp;
import org.vstu.meaningtree.nodes.expressions.math.*;
import org.vstu.meaningtree.nodes.expressions.other.*;
import org.vstu.meaningtree.nodes.expressions.unary.*;
import org.vstu.meaningtree.nodes.statements.CompoundStatement;
import org.vstu.meaningtree.nodes.statements.ExpressionStatement;
import org.vstu.meaningtree.nodes.statements.assignments.AssignmentStatement;
import org.vstu.meaningtree.nodes.statements.conditions.IfStatement;
import org.vstu.meaningtree.nodes.statements.conditions.SwitchStatement;
import org.vstu.meaningtree.nodes.statements.loops.DoWhileLoop;
import org.vstu.meaningtree.nodes.statements.loops.GeneralForLoop;
import org.vstu.meaningtree.nodes.statements.loops.RangeForLoop;
import org.vstu.meaningtree.nodes.statements.loops.WhileLoop;
import org.vstu.meaningtree.nodes.statements.loops.control.BreakStatement;
import org.vstu.meaningtree.nodes.statements.loops.control.ContinueStatement;

public class JsonViewer extends LanguageViewer {
    @Override
    public String toString(Node node) {
        JsonObject json = switch (node) {
            // Operators
            case AddOp op -> toJson(op);
            case SubOp op -> toJson(op);
            case MulOp op -> toJson(op);
            case DivOp op -> toJson(op);
            case ModOp op -> toJson(op);
            case MatMulOp op -> toJson(op);
            case FloorDivOp op -> toJson(op);
            case EqOp op -> toJson(op);
            case GeOp op -> toJson(op);
            case GtOp op -> toJson(op);
            case LeOp op -> toJson(op);
            case LtOp op -> toJson(op);
            case UnaryMinusOp unaryMinusOp -> toJson(unaryMinusOp);
            case UnaryPlusOp unaryPlusOp -> toJson(unaryPlusOp);
            case InstanceOfOp op -> toJson(op);
            case NotEqOp op -> toJson(op);
            case ShortCircuitAndOp op -> toJson(op);
            case ShortCircuitOrOp op -> toJson(op);
            case PowOp op -> toJson(op);
            case NotOp op -> toJson(op);
            case PostfixIncrementOp inc -> toJson(inc);
            case PostfixDecrementOp dec -> toJson(dec);
            case PrefixIncrementOp inc -> toJson(inc);
            case PrefixDecrementOp dec -> toJson(dec);
            case BitwiseAndOp bitwiseAndOp -> toJson(bitwiseAndOp);
            case BitwiseOrOp bitwiseOrOp -> toJson(bitwiseOrOp);
            case XorOp xorOp -> toJson(xorOp);
            case InversionOp inversionOp -> toJson(inversionOp);
            case LeftShiftOp leftShiftOp -> toJson(leftShiftOp);
            case RightShiftOp rightShiftOp -> toJson(rightShiftOp);
            case ContainsOp op -> toJson(op);
            case ReferenceEqOp op -> toJson(op);
            case TernaryOperator ternaryOperator -> toJson(ternaryOperator);

            // Literals
            case FloatLiteral l -> toJson(l);
            case IntegerLiteral l -> toJson(l);
            case StringLiteral l -> toJson(l);
            case NullLiteral l -> toJson(l);
            case BoolLiteral l -> toJson(l);
            case CharacterLiteral l -> toJson(l);

            // Expressions
            case ParenthesizedExpression expr -> toJson(expr);
            case SimpleIdentifier expr -> toJson(expr);
            case AssignmentExpression expr -> toJson(expr);
            case CompoundComparison cmp -> toJson(cmp);
            case FunctionCall funcCall -> toJson(funcCall);
            case IndexExpression indexExpression -> toJson(indexExpression);

            // Statements
            case AssignmentStatement stmt -> toJson(stmt);
            case VariableDeclaration stmt -> toJson(stmt);
            case CompoundStatement stmt -> toJson(stmt);
            case ExpressionStatement stmt -> toJson(stmt);
            case IfStatement stmt -> toJson(stmt);
            case GeneralForLoop stmt -> toJson(stmt);
            case RangeForLoop rangeLoop -> toJson(rangeLoop);
            case WhileLoop whileLoop -> toJson(whileLoop);
            case BreakStatement stmt -> toJson(stmt);
            case ContinueStatement stmt -> toJson(stmt);
            case SwitchStatement switchStatement -> toJson(switchStatement);
            case DoWhileLoop doWhileLoop -> toJson(doWhileLoop);

            case ProgramEntryPoint entryPoint -> toJson(entryPoint);
            case Comment comment -> toJson(comment);

            default -> throw new IllegalStateException("Unexpected value: " + node);
        };

        return json.toString();
    }


    /* -----------------------------
    |          Operators            |
    ------------------------------ */

    @NotNull
    private JsonObject toJson(@NotNull AddOp op) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "add_operator");
        json.add("left_operand", toJson(op.getLeft()));
        json.add("right_operand", toJson(op.getRight()));

        return json;
    }

    @NotNull
    private JsonObject toJson(@NotNull SubOp op) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "sub_operator");
        json.add("left_operand", toJson(op.getLeft()));
        json.add("right_operand", toJson(op.getRight()));

        return json;
    }

    @NotNull
    private JsonObject toJson(@NotNull MulOp op) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "mul_operator");
        json.add("left_operand", toJson(op.getLeft()));
        json.add("right_operand", toJson(op.getRight()));

        return json;
    }

    @NotNull
    private JsonObject toJson(@NotNull DivOp op) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "div_operator");
        json.add("left_operand", toJson(op.getLeft()));
        json.add("right_operand", toJson(op.getRight()));

        return json;
    }

    @NotNull
    private JsonObject toJson(@NotNull ModOp op) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "mod_operator");
        json.add("left_operand", toJson(op.getLeft()));
        json.add("right_operand", toJson(op.getRight()));

        return json;
    }

    @NotNull
    private JsonObject toJson(@NotNull MatMulOp op) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "matrix_mul_operator");
        json.add("left_operand", toJson(op.getLeft()));
        json.add("right_operand", toJson(op.getRight()));

        return json;
    }

    @NotNull
    private JsonObject toJson(@NotNull FloorDivOp op) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "floor_div_operator");
        json.add("left_operand", toJson(op.getLeft()));
        json.add("right_operand", toJson(op.getRight()));

        return json;
    }

    @NotNull
    private JsonObject toJson(@NotNull PowOp op) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "pow_operator");
        json.add("left_operand", toJson(op.getLeft()));
        json.add("right_operand", toJson(op.getRight()));

        return json;
    }

    @NotNull
    private JsonObject toJson(@NotNull EqOp op) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "eq_operator");
        json.add("left_operand", toJson(op.getLeft()));
        json.add("right_operand", toJson(op.getRight()));

        return json;
    }

    @NotNull
    private JsonObject toJson(@NotNull GeOp op) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "ge_operator");
        json.add("left_operand", toJson(op.getLeft()));
        json.add("right_operand", toJson(op.getRight()));

        return json;
    }

    @NotNull
    private JsonObject toJson(@NotNull GtOp op) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "gt_operator");
        json.add("left_operand", toJson(op.getLeft()));
        json.add("right_operand", toJson(op.getRight()));

        return json;
    }

    @NotNull
    private JsonObject toJson(@NotNull LeOp op) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "le_operator");
        json.add("left_operand", toJson(op.getLeft()));
        json.add("right_operand", toJson(op.getRight()));

        return json;
    }

    @NotNull
    private JsonObject toJson(@NotNull LtOp op) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "lt_operator");
        json.add("left_operand", toJson(op.getLeft()));
        json.add("right_operand", toJson(op.getRight()));

        return json;
    }

    @NotNull
    private JsonObject toJson(@NotNull NotEqOp op) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "not_eq_operator");
        json.add("left_operand", toJson(op.getLeft()));
        json.add("right_operand", toJson(op.getRight()));

        return json;
    }

    @NotNull
    private JsonObject toJson(@NotNull ReferenceEqOp op) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "reference_eq_operator");
        json.add("left_operand", toJson(op.getLeft()));
        json.add("right_operand", toJson(op.getRight()));

        return json;
    }

    @NotNull
    private JsonObject toJson(@NotNull ShortCircuitAndOp op) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "short_circuit_and_operator");
        json.add("left_operand", toJson(op.getLeft()));
        json.add("right_operand", toJson(op.getRight()));

        return json;
    }

    @NotNull
    private JsonObject toJson(@NotNull ShortCircuitOrOp op) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "short_circuit_or_operator");
        json.add("left_operand", toJson(op.getLeft()));
        json.add("right_operand", toJson(op.getRight()));

        return json;
    }

    @NotNull
    private JsonObject toJson(@NotNull NotOp op) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "unary_operator");
        json.add("operand", toJson(op.getArgument()));

        return json;
    }

    @NotNull
    private JsonObject toJson(@NotNull UnaryMinusOp op) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "unary_minus_operator");
        json.add("operand", toJson(op.getArgument()));
        return json;
    }

    @NotNull
    private JsonObject toJson(@NotNull UnaryPlusOp op) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "unary_plus_operator");
        json.add("operand", toJson(op.getArgument()));

        return json;
    }

    @NotNull
    private JsonObject toJson(@NotNull PostfixIncrementOp op) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "unary_postfix_inc_operator");
        json.add("operand", toJson(op.getArgument()));

        return json;
    }

    @NotNull
    private JsonObject toJson(@NotNull PostfixDecrementOp op) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "unary_postfix_dec_operator");
        json.add("operand", toJson(op.getArgument()));

        return json;
    }

    @NotNull
    private JsonObject toJson(@NotNull PrefixIncrementOp op) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "unary_prefix_inc_operator");
        json.add("operand", toJson(op.getArgument()));

        return json;
    }

    @NotNull
    private JsonObject toJson(@NotNull PrefixDecrementOp op) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "unary_prefix_dec_operator");
        json.add("operand", toJson(op.getArgument()));

        return json;
    }

    @NotNull
    private JsonObject toJson(@NotNull BitwiseAndOp op) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "bitwise_and_operator");
        json.add("left_operand", toJson(op.getLeft()));
        json.add("right_operand", toJson(op.getRight()));

        return json;
    }

    @NotNull
    private JsonObject toJson(@NotNull BitwiseOrOp op) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "bitwise_or_operator");
        json.add("left_operand", toJson(op.getLeft()));
        json.add("right_operand", toJson(op.getRight()));

        return json;
    }

    @NotNull
    private JsonObject toJson(@NotNull XorOp op) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "xor_operator");
        json.add("left_operand", toJson(op.getLeft()));
        json.add("right_operand", toJson(op.getRight()));

        return json;
    }

    @NotNull
    private JsonObject toJson(@NotNull InversionOp op) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "inversion_operator");
        json.add("operand", toJson(op.getArgument()));

        return json;
    }

    @NotNull
    private JsonObject toJson(@NotNull LeftShiftOp op) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "left_shift_operator");
        json.add("left_operand", toJson(op.getLeft()));
        json.add("right_operand", toJson(op.getRight()));

        return json;
    }

    @NotNull
    private JsonObject toJson(@NotNull RightShiftOp op) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "right_shift_operator");
        json.add("left_operand", toJson(op.getLeft()));
        json.add("right_operand", toJson(op.getRight()));

        return json;
    }

    @NotNull
    private JsonObject toJson(@NotNull InstanceOfOp op) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "instance_of_operator");
        json.add("expression", toJson(op.getLeft()));
        json.add("type", toJson(op.getRight()));

        return json;
    }

    @NotNull
    private JsonObject toJson(@NotNull ContainsOp op) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "contains_operator");
        json.add("element", toJson(op.getLeft()));
        json.add("collection", toJson(op.getRight()));

        return json;
    }

    @NotNull
    private JsonObject toJson(@NotNull TernaryOperator op) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "ternary_operator");
        json.add("condition", toJson(op.getCondition()));
        json.add("true_expression", toJson(op.getThenExpr()));
        json.add("false_expression", toJson(op.getElseExpr()));

        return json;
    }

    /* -----------------------------
    |           Literals            |
    ------------------------------ */

    @NotNull
    private JsonObject toJson(@NotNull FloatLiteral floatLiteral) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "float_literal");
        json.addProperty("value", floatLiteral.getValue());
        json.addProperty("is_double", floatLiteral.isDoublePrecision());

        return json;
    }

    @NotNull
    private JsonObject toJson(@NotNull IntegerLiteral integerLiteral) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "int_literal");
        json.addProperty("value", integerLiteral.getLongValue());
        json.addProperty("repr", integerLiteral.getIntegerRepresentation().toString());

        return json;
    }

    @NotNull
    private JsonObject toJson(@NotNull StringLiteral stringLiteral) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "string_literal");
        json.addProperty("value", stringLiteral.getUnescapedValue());

        return json;
    }

    @NotNull
    private JsonObject toJson(@NotNull NullLiteral nullLiteral) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "null_literal");

        return json;
    }

    @NotNull
    private JsonObject toJson(@NotNull BoolLiteral boolLiteral) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "bool_literal");
        json.addProperty("value", boolLiteral.getValue());

        return json;
    }

    @NotNull
    private JsonObject toJson(@NotNull CharacterLiteral characterLiteral) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "char_literal");
        json.addProperty("value", characterLiteral.getValue());

        return json;
    }

}
