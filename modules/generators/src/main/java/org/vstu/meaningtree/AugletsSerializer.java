package org.vstu.meaningtree;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Node;
import org.vstu.meaningtree.nodes.Type;
import org.vstu.meaningtree.nodes.declarations.VariableDeclaration;
import org.vstu.meaningtree.nodes.expressions.BinaryExpression;
import org.vstu.meaningtree.nodes.expressions.UnaryExpression;
import org.vstu.meaningtree.nodes.expressions.comparison.*;
import org.vstu.meaningtree.nodes.expressions.identifiers.SimpleIdentifier;
import org.vstu.meaningtree.nodes.expressions.logical.ShortCircuitAndOp;
import org.vstu.meaningtree.nodes.expressions.logical.ShortCircuitOrOp;
import org.vstu.meaningtree.nodes.expressions.math.*;
import org.vstu.meaningtree.nodes.expressions.unary.*;
import org.vstu.meaningtree.nodes.types.builtin.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AugletsSerializer {
    private AugletsMeta _meta;
    private int _variableNumber = 0;
    private final Map<String, String> _variableMapping = new HashMap<>();
    private int _typeNumber = 0;
    private final Map<String, String> _typeMapping = new HashMap<>();

    public String serialize(AugletProblem problem) {
        if (_meta == null) {
            throw new IllegalStateException("Meta was not set. Please call AugletsSerializer.setMeta() first");
        }

        var problemStr = toString(problem.problemMeaningTree().getRootNode(), problem.meta().uniqueProblemNodes());
        var solutionStr = toString(problem.solutionMeaningTree().getRootNode(), problem.meta().uniqueSolutionNodes());

        _variableMapping.clear();
        _typeMapping.clear();
        return problemStr + solutionStr;
    }

    // Наладить диспетчеризацию как нибудь
    private String toString(Node node, List<Node> uniqueNodes) {
        return switch (node) {
            case VariableDeclaration varDecl -> toString(varDecl);
            default -> throw new IllegalStateException(String.format("Can't stringify node %s", node.getClass()));
        };
    }

    private String toString(VariableDeclaration varDecl) {
        if (varDecl.getDeclarators().length != 1) {
            throw new IllegalStateException(String.format("Can't process node %s. Multiple declaration isn't supported", varDecl.getClass()));
        }

        var decl = varDecl.getDeclarators()[0];
        var expr = toString(decl.getRValue());

        return type(varDecl.getType()) + variable(decl.getIdentifier());
    }

    private String toString(Expression expression) {
        return switch (expression) {
            case BinaryExpression binaryExpression -> toString(binaryExpression);
            case UnaryExpression unaryExpression -> toString(unaryExpression);
            default -> throw new IllegalStateException(
                    String.format("Expression %s is not supported", expression.getClass())
            );
        };
    }

    private String toString(BinaryExpression binaryExpression) {
        var opSymbol = switch (binaryExpression) {
            case AddOp _               -> "+";
            case SubOp _               -> "-";
            case MulOp _               -> "*";
            case DivOp _               -> "/";
            case ModOp _               -> "%";
            case FloorDivOp _          -> "/";
            case EqOp _                -> "==";
            case NotEqOp _             -> "!=";
            case GeOp _                -> ">=";
            case GtOp _                -> ">";
            case LeOp _                -> "<=";
            case LtOp _                -> "<";
            case ShortCircuitAndOp _   -> "&&";
            case ShortCircuitOrOp _    -> "||";
            default -> throw new IllegalArgumentException(
                    "Unknown binary operator: " + binaryExpression.getClass().getSimpleName());
        };

        return toString(binaryExpression, opSymbol);
    }

    private String toString(BinaryExpression expr, String opSymbol) {
        String left  = toString(expr.getLeft());
        String right = toString(expr.getRight());
        return String.format("%s %s %s", left, opSymbol, right);
    }

    private String toString(UnaryExpression expr) {
        var arg = expr.getArgument();
        return switch (expr) {
            case UnaryPlusOp _ -> "+" + toString(arg);
            case UnaryMinusOp _ -> "-" + toString(arg);
            case PrefixIncrementOp _ -> "++" + toString(arg);
            case PrefixDecrementOp _ -> "--" + toString(arg);
            case PostfixIncrementOp _ -> toString(arg) + "++";
            case PostfixDecrementOp _ -> toString(arg) + "--";
            default -> throw new IllegalStateException("Unexpected value: " + expr);
        };
    }

    private String type(Type type) {
        var typeName = switch (type) {
            case IntType t -> "int";
            case StringType t -> "string";
            case FloatType t -> "float";
            case BooleanType t -> "boolean";
            case CharacterType t -> "char";
            default -> throw new IllegalStateException(String.format("Type %s is not supported", type.getClass()));
        };

        if (_typeMapping.containsKey(typeName)) {
            return _typeMapping.get(typeName);
        }

        var res =  "<T" + _typeNumber + "#" + typeName + "#>";
        _typeNumber++;
        _typeMapping.put(typeName, res);

        return res;
    }

    private String variable(SimpleIdentifier identifier) {
        var varName = identifier.toString();

        if (_variableMapping.containsKey(varName)) {
            return _variableMapping.get(varName);
        }

        var res =  "<" + _variableNumber + "#" + varName + "#>";
        _variableNumber++;
        _variableMapping.put(varName, res);

        return res;
    }

}
