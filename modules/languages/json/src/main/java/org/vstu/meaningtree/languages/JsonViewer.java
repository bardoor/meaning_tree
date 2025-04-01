package org.vstu.meaningtree.languages;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import jakarta.json.Json;
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
            case NullLiteral nullLiteral -> toJson(nullLiteral);
            case BoolLiteral boolLiteral -> toJson(boolLiteral);
            case CharacterLiteral characterLiteral -> toJson(characterLiteral);

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

}
