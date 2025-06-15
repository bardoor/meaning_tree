package org.vstu.meaningtree.serializers.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.vstu.meaningtree.MeaningTree;
import org.vstu.meaningtree.nodes.Comment;
import org.vstu.meaningtree.nodes.Node;
import org.vstu.meaningtree.nodes.ProgramEntryPoint;
import org.vstu.meaningtree.nodes.declarations.VariableDeclaration;
import org.vstu.meaningtree.nodes.declarations.components.VariableDeclarator;
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
import org.vstu.meaningtree.nodes.statements.conditions.components.ConditionBranch;
import org.vstu.meaningtree.nodes.statements.loops.DoWhileLoop;
import org.vstu.meaningtree.nodes.statements.loops.GeneralForLoop;
import org.vstu.meaningtree.nodes.statements.loops.RangeForLoop;
import org.vstu.meaningtree.nodes.statements.loops.WhileLoop;
import org.vstu.meaningtree.nodes.statements.loops.control.BreakStatement;
import org.vstu.meaningtree.nodes.statements.loops.control.ContinueStatement;
import org.vstu.meaningtree.serializers.model.Serializer;

import java.util.Objects;

public class JsonSerializer implements Serializer<JsonObject> {
    @Override
    public JsonObject serialize(MeaningTree mt) {
        return serialize(mt.getRootNode());
    }


    @Override
    public JsonObject serialize(Node node) {
        var json = switch (node) {
            // Operators
            case AddOp op -> serialize(op);
            case SubOp op -> serialize(op);
            case MulOp op -> serialize(op);
            case DivOp op -> serialize(op);
            case ModOp op -> serialize(op);
            case MatMulOp op -> serialize(op);
            case FloorDivOp op -> serialize(op);
            case EqOp op -> serialize(op);
            case GeOp op -> serialize(op);
            case GtOp op -> serialize(op);
            case LeOp op -> serialize(op);
            case LtOp op -> serialize(op);
            case UnaryMinusOp unaryMinusOp -> serialize(unaryMinusOp);
            case UnaryPlusOp unaryPlusOp -> serialize(unaryPlusOp);
            case InstanceOfOp op -> serialize(op);
            case NotEqOp op -> serialize(op);
            case ShortCircuitAndOp op -> serialize(op);
            case ShortCircuitOrOp op -> serialize(op);
            case PowOp op -> serialize(op);
            case NotOp op -> serialize(op);
            case PostfixIncrementOp inc -> serialize(inc);
            case PostfixDecrementOp dec -> serialize(dec);
            case PrefixIncrementOp inc -> serialize(inc);
            case PrefixDecrementOp dec -> serialize(dec);
            case BitwiseAndOp bitwiseAndOp -> serialize(bitwiseAndOp);
            case BitwiseOrOp bitwiseOrOp -> serialize(bitwiseOrOp);
            case XorOp xorOp -> serialize(xorOp);
            case InversionOp inversionOp -> serialize(inversionOp);
            case LeftShiftOp leftShiftOp -> serialize(leftShiftOp);
            case RightShiftOp rightShiftOp -> serialize(rightShiftOp);
            case ContainsOp op -> serialize(op);
            case ReferenceEqOp op -> serialize(op);
            case TernaryOperator ternaryOperator -> serialize(ternaryOperator);

            // Literals
            case FloatLiteral l -> serialize(l);
            case IntegerLiteral l -> serialize(l);
            case StringLiteral l -> serialize(l);
            case NullLiteral l -> serialize(l);
            case BoolLiteral l -> serialize(l);
            case CharacterLiteral l -> serialize(l);

            // Expressions
            case ParenthesizedExpression expr -> serialize(expr);
            case SimpleIdentifier expr -> serialize(expr);
            case AssignmentExpression expr -> serialize(expr);
            case CompoundComparison cmp -> serialize(cmp);
            case FunctionCall funcCall -> serialize(funcCall);
            case IndexExpression indexExpression -> serialize(indexExpression);
            case Range range -> serialize(range);

            // Statements
            case AssignmentStatement stmt -> serialize(stmt);
            case VariableDeclaration stmt -> serialize(stmt);
            case CompoundStatement stmt -> serialize(stmt);
            case ExpressionStatement stmt -> serialize(stmt);
            case IfStatement stmt -> serialize(stmt);
            case ConditionBranch stmt -> serialize(stmt);
            case GeneralForLoop stmt -> serialize(stmt);
            case RangeForLoop rangeLoop -> serialize(rangeLoop);
            case WhileLoop whileLoop -> serialize(whileLoop);
            case BreakStatement stmt -> serialize(stmt);
            case ContinueStatement stmt -> serialize(stmt);
            case SwitchStatement switchStatement -> serialize(switchStatement);
            case DoWhileLoop doWhileLoop -> serialize(doWhileLoop);

            case ProgramEntryPoint entryPoint -> serialize(entryPoint);
            case Comment comment -> serialize(comment);

            default -> throw new IllegalStateException("Unexpected value: " + node);
        };

        json.addProperty("id", node.getId());

        return json;
    }

    /* -----------------------------
    |          Operators            |
    ------------------------------ */

    @NotNull
    private JsonObject serialize(@NotNull AddOp op) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "add_operator");
        json.add("left_operand", serialize(op.getLeft()));
        json.add("right_operand", serialize(op.getRight()));

        return json;
    }

    @NotNull
    private JsonObject serialize(@NotNull SubOp op) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "sub_operator");
        json.add("left_operand", serialize(op.getLeft()));
        json.add("right_operand", serialize(op.getRight()));

        return json;
    }

    @NotNull
    private JsonObject serialize(@NotNull MulOp op) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "mul_operator");
        json.add("left_operand", serialize(op.getLeft()));
        json.add("right_operand", serialize(op.getRight()));

        return json;
    }

    @NotNull
    private JsonObject serialize(@NotNull DivOp op) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "div_operator");
        json.add("left_operand", serialize(op.getLeft()));
        json.add("right_operand", serialize(op.getRight()));

        return json;
    }

    @NotNull
    private JsonObject serialize(@NotNull ModOp op) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "mod_operator");
        json.add("left_operand", serialize(op.getLeft()));
        json.add("right_operand", serialize(op.getRight()));

        return json;
    }

    @NotNull
    private JsonObject serialize(@NotNull MatMulOp op) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "matrix_mul_operator");
        json.add("left_operand", serialize(op.getLeft()));
        json.add("right_operand", serialize(op.getRight()));

        return json;
    }

    @NotNull
    private JsonObject serialize(@NotNull FloorDivOp op) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "floor_div_operator");
        json.add("left_operand", serialize(op.getLeft()));
        json.add("right_operand", serialize(op.getRight()));

        return json;
    }

    @NotNull
    private JsonObject serialize(@NotNull PowOp op) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "pow_operator");
        json.add("left_operand", serialize(op.getLeft()));
        json.add("right_operand", serialize(op.getRight()));

        return json;
    }

    @NotNull
    private JsonObject serialize(@NotNull EqOp op) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "eq_operator");
        json.add("left_operand", serialize(op.getLeft()));
        json.add("right_operand", serialize(op.getRight()));

        return json;
    }

    @NotNull
    private JsonObject serialize(@NotNull GeOp op) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "ge_operator");
        json.add("left_operand", serialize(op.getLeft()));
        json.add("right_operand", serialize(op.getRight()));

        return json;
    }

    @NotNull
    private JsonObject serialize(@NotNull GtOp op) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "gt_operator");
        json.add("left_operand", serialize(op.getLeft()));
        json.add("right_operand", serialize(op.getRight()));

        return json;
    }

    @NotNull
    private JsonObject serialize(@NotNull LeOp op) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "le_operator");
        json.add("left_operand", serialize(op.getLeft()));
        json.add("right_operand", serialize(op.getRight()));

        return json;
    }

    @NotNull
    private JsonObject serialize(@NotNull LtOp op) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "lt_operator");
        json.add("left_operand", serialize(op.getLeft()));
        json.add("right_operand", serialize(op.getRight()));

        return json;
    }

    @NotNull
    private JsonObject serialize(@NotNull NotEqOp op) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "not_eq_operator");
        json.add("left_operand", serialize(op.getLeft()));
        json.add("right_operand", serialize(op.getRight()));

        return json;
    }

    @NotNull
    private JsonObject serialize(@NotNull ReferenceEqOp op) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "reference_eq_operator");
        json.add("left_operand", serialize(op.getLeft()));
        json.add("right_operand", serialize(op.getRight()));

        return json;
    }

    @NotNull
    private JsonObject serialize(@NotNull ShortCircuitAndOp op) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "short_circuit_and_operator");
        json.add("left_operand", serialize(op.getLeft()));
        json.add("right_operand", serialize(op.getRight()));

        return json;
    }

    @NotNull
    private JsonObject serialize(@NotNull ShortCircuitOrOp op) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "short_circuit_or_operator");
        json.add("left_operand", serialize(op.getLeft()));
        json.add("right_operand", serialize(op.getRight()));

        return json;
    }

    @NotNull
    private JsonObject serialize(@NotNull NotOp op) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "unary_operator");
        json.add("operand", serialize(op.getArgument()));

        return json;
    }

    @NotNull
    private JsonObject serialize(@NotNull UnaryMinusOp op) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "unary_minus_operator");
        json.add("operand", serialize(op.getArgument()));
        return json;
    }

    @NotNull
    private JsonObject serialize(@NotNull UnaryPlusOp op) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "unary_plus_operator");
        json.add("operand", serialize(op.getArgument()));

        return json;
    }

    @NotNull
    private JsonObject serialize(@NotNull PostfixIncrementOp op) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "unary_postfix_inc_operator");
        json.add("operand", serialize(op.getArgument()));

        return json;
    }

    @NotNull
    private JsonObject serialize(@NotNull PostfixDecrementOp op) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "unary_postfix_dec_operator");
        json.add("operand", serialize(op.getArgument()));

        return json;
    }

    @NotNull
    private JsonObject serialize(@NotNull PrefixIncrementOp op) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "unary_prefix_inc_operator");
        json.add("operand", serialize(op.getArgument()));

        return json;
    }

    @NotNull
    private JsonObject serialize(@NotNull PrefixDecrementOp op) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "unary_prefix_dec_operator");
        json.add("operand", serialize(op.getArgument()));

        return json;
    }

    @NotNull
    private JsonObject serialize(@NotNull BitwiseAndOp op) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "bitwise_and_operator");
        json.add("left_operand", serialize(op.getLeft()));
        json.add("right_operand", serialize(op.getRight()));

        return json;
    }

    @NotNull
    private JsonObject serialize(@NotNull BitwiseOrOp op) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "bitwise_or_operator");
        json.add("left_operand", serialize(op.getLeft()));
        json.add("right_operand", serialize(op.getRight()));

        return json;
    }

    @NotNull
    private JsonObject serialize(@NotNull XorOp op) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "xor_operator");
        json.add("left_operand", serialize(op.getLeft()));
        json.add("right_operand", serialize(op.getRight()));

        return json;
    }

    @NotNull
    private JsonObject serialize(@NotNull InversionOp op) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "inversion_operator");
        json.add("operand", serialize(op.getArgument()));

        return json;
    }

    @NotNull
    private JsonObject serialize(@NotNull LeftShiftOp op) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "left_shift_operator");
        json.add("left_operand", serialize(op.getLeft()));
        json.add("right_operand", serialize(op.getRight()));

        return json;
    }

    @NotNull
    private JsonObject serialize(@NotNull RightShiftOp op) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "right_shift_operator");
        json.add("left_operand", serialize(op.getLeft()));
        json.add("right_operand", serialize(op.getRight()));

        return json;
    }

    @NotNull
    private JsonObject serialize(@NotNull InstanceOfOp op) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "instance_of_operator");
        json.add("expression", serialize(op.getLeft()));
        json.add("type", serialize(op.getRight()));

        return json;
    }

    @NotNull
    private JsonObject serialize(@NotNull ContainsOp op) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "contains_operator");
        json.add("element", serialize(op.getLeft()));
        json.add("collection", serialize(op.getRight()));

        return json;
    }

    @NotNull
    private JsonObject serialize(@NotNull TernaryOperator op) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "ternary_operator");
        json.add("condition", serialize(op.getCondition()));
        json.add("true_expression", serialize(op.getThenExpr()));
        json.add("false_expression", serialize(op.getElseExpr()));

        return json;
    }

    /* -----------------------------
    |           Literals            |
    ------------------------------ */

    @NotNull
    private JsonObject serialize(@NotNull FloatLiteral floatLiteral) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "float_literal");
        json.addProperty("value", floatLiteral.getValue());
        json.addProperty("is_double", floatLiteral.isDoublePrecision());

        return json;
    }

    @NotNull
    private JsonObject serialize(@NotNull IntegerLiteral integerLiteral) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "int_literal");
        json.addProperty("value", integerLiteral.getLongValue());
        json.addProperty("repr", integerLiteral.getIntegerRepresentation().toString());

        return json;
    }

    @NotNull
    private JsonObject serialize(@NotNull StringLiteral stringLiteral) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "string_literal");
        json.addProperty("value", stringLiteral.getUnescapedValue());

        return json;
    }

    @NotNull
    private JsonObject serialize(@NotNull NullLiteral nullLiteral) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "null_literal");

        return json;
    }

    @NotNull
    private JsonObject serialize(@NotNull BoolLiteral boolLiteral) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "bool_literal");
        json.addProperty("value", boolLiteral.getValue());

        return json;
    }

    @NotNull
    private JsonObject serialize(@NotNull CharacterLiteral characterLiteral) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "char_literal");
        json.addProperty("value", characterLiteral.getValue());

        return json;
    }


    /* -----------------------------
    |         Expressions           |
    ------------------------------ */

    @NotNull
    private JsonObject serialize(@NotNull ParenthesizedExpression expr) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "parenthesized_expression");
        json.add("expression", serialize(expr.getExpression()));

        return json;
    }

    @NotNull
    private JsonObject serialize(@NotNull SimpleIdentifier expr) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "identifier");
        json.addProperty("name", expr.getName());

        return json;
    }

    @NotNull
    private JsonObject serialize(@NotNull AssignmentExpression expr) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "assignment_expression");
        json.add("target", serialize(expr.getLValue()));
        json.add("value", serialize(expr.getRValue()));

        return json;
    }

    // TODO: Я думаю это не работает но сейчас 2 часа ночи...
    @NotNull
    private JsonObject serialize(@NotNull CompoundComparison cmp) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "compound_comparison");

        JsonArray comparisons = new JsonArray();
        for (var comparison : cmp.getComparisons()) {
            JsonObject jsonComparison = new JsonObject();
            jsonComparison.add("left", serialize(comparison.getLeft()));
            jsonComparison.addProperty("operator", comparison.getClass().getSimpleName());
            jsonComparison.add("right", serialize(comparison.getRight()));
        }

        json.add("comparisons", comparisons);
        return json;
    }

    @NotNull
    private JsonObject serialize(@NotNull FunctionCall funcCall) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "function_call");
        json.add("function", serialize(funcCall.getFunction()));

        JsonArray args = new JsonArray();
        for (var arg : funcCall.getArguments()) {
            args.add(serialize(arg));
        }

        json.add("arguments", args);
        return json;
    }

    @NotNull
    private JsonObject serialize(@NotNull IndexExpression indexExpression) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "index_expression");
        json.add("expr", serialize(indexExpression.getExpression()));
        json.add("index", serialize(indexExpression.getIndex()));

        return json;
    }


    /* -----------------------------
    |          Statements           |
    ------------------------------ */

    @NotNull
    private JsonObject serialize(@NotNull AssignmentStatement stmt) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "assignment_statement");
        json.add("target", serialize(stmt.getLValue()));
        json.add("value", serialize(stmt.getRValue()));

        return json;
    }

    @NotNull
    private JsonObject serialize(@NotNull VariableDeclaration stmt) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "variable_declaration");

        JsonArray declarators = new JsonArray();
        for (VariableDeclarator varDecl : stmt.getDeclarators()) {
            JsonObject jsonDeclaration = new JsonObject();
            jsonDeclaration.add("identifier", serialize(varDecl.getIdentifier()));
            if (varDecl.getRValue() != null) {
                jsonDeclaration.add("rvalue", serialize(varDecl.getRValue()));
            }
            declarators.add(jsonDeclaration);
        }

        json.add("declarators", declarators);
        return json;
    }

    @NotNull
    private JsonObject serialize(@NotNull CompoundStatement stmt) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "compound_statement");

        JsonArray statements = new JsonArray();
        for (var statement : stmt.getNodes()) {
            statements.add(serialize(statement));
        }

        json.add("statements", statements);
        return json;
    }

    @NotNull
    private JsonObject serialize(@NotNull ExpressionStatement stmt) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "expression_statement");
        json.add("expression", serialize(stmt.getExpression()));

        return json;
    }

    @NotNull
    private JsonObject serialize(@NotNull IfStatement stmt) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "if_statement");

        JsonArray branches = new JsonArray();
        for (var branch : stmt.getBranches()) {
            branches.add(serialize(branch));
        }

        json.add("branches", branches);
        return json;
    }

    @NotNull
    private JsonObject serialize(@NotNull ConditionBranch branch) {
        JsonObject branchJson = new JsonObject();

        if (branch.getCondition() != null) {
            branchJson.add("condition", serialize(branch.getCondition()));
        }

        branchJson.add("body", serialize(branch.getBody()));
        branchJson.addProperty("id", branch.getId());
        branchJson.addProperty("type", "condition_branch");

        return branchJson;
    }


    // TODO: сделать это как таковое
    @NotNull
    private JsonObject serialize(@NotNull GeneralForLoop stmt) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "general_for_loop");

        /*
        if (stmt.getInitializer() != null) {
            json.add("initializer", serialize(stmt.getInitializer()));
        }
         */

        if (stmt.getCondition() != null) {
            json.add("condition", serialize(stmt.getCondition()));
        }

        if (stmt.getUpdate() != null) {
            json.add("update", serialize(stmt.getUpdate()));
        }

        json.add("body", serialize(stmt.getBody()));
        return json;
    }

    @NotNull
    private JsonObject serialize(@NotNull Range range) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "range");
        json.add("start", serialize(range.getStart()));
        json.add("stop", serialize(range.getStop()));
        json.add("step", serialize(range.getStep()));

        json.addProperty("isExcludingStart", range.isExcludingStart());
        json.addProperty("isExcludingEnd", range.isExcludingEnd());

        json.addProperty("rangeType", range.getType().name().toLowerCase());

        return json;
    }

    @NotNull
    private JsonObject serialize(@NotNull RangeForLoop stmt) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "range_for_loop");
        json.add("identifier", serialize(stmt.getIdentifier()));
        json.add("range", serialize(stmt.getRange()));
        json.add("body", serialize(stmt.getBody()));

        return json;
    }

    @NotNull
    private JsonObject serialize(@NotNull WhileLoop stmt) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "while_loop");
        json.add("condition", serialize(stmt.getCondition()));
        json.add("body", serialize(stmt.getBody()));

        return json;
    }

    @NotNull
    private JsonObject serialize(@NotNull BreakStatement stmt) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "break_statement");

        return json;
    }

    @NotNull
    private JsonObject serialize(@NotNull ContinueStatement stmt) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "continue_statement");

        return json;
    }

    @NotNull
    private JsonObject serialize(@NotNull SwitchStatement stmt) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "switch_statement");
        json.add("expression", serialize(stmt.getTargetExpression()));

        JsonArray cases = new JsonArray();
        for (var switchCase : stmt.getCases()) {
            cases.add(serialize(switchCase.getBody()));
        }

        if (stmt.hasDefaultCase()) {
            json.add("default", serialize(Objects.requireNonNull(stmt.getDefaultCase())));
        }

        json.add("cases", cases);
        return json;
    }

    @NotNull
    private JsonObject serialize(@NotNull DoWhileLoop stmt) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "do_while_loop");
        json.add("body", serialize(stmt.getBody()));
        json.add("condition", serialize(stmt.getCondition()));

        return json;
    }


    /* -----------------------------
    |            Other              |
    ------------------------------ */

    @NotNull
    private JsonObject serialize(@NotNull ProgramEntryPoint entryPoint) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "program_entry_point");

        JsonArray body = new JsonArray();
        for (var entry : entryPoint.getBody()) {
            body.add(serialize(entry));
        }

        json.add("body", body);
        return json;
    }

    @NotNull
    private JsonObject serialize(@NotNull Comment comment) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "comment");
        json.addProperty("content", comment.getUnescapedContent());
        json.addProperty("is_multiline", comment.isMultiline());

        return json;
    }

}
