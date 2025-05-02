package org.vstu.meaningtree;

import org.vstu.meaningtree.nodes.Node;
import org.vstu.meaningtree.nodes.ProgramEntryPoint;
import org.vstu.meaningtree.nodes.Type;
import org.vstu.meaningtree.nodes.declarations.VariableDeclaration;
import org.vstu.meaningtree.nodes.expressions.BinaryExpression;
import org.vstu.meaningtree.nodes.expressions.UnaryExpression;
import org.vstu.meaningtree.nodes.expressions.comparison.*;
import org.vstu.meaningtree.nodes.expressions.identifiers.SimpleIdentifier;
import org.vstu.meaningtree.nodes.expressions.literals.NumericLiteral;
import org.vstu.meaningtree.nodes.expressions.logical.ShortCircuitAndOp;
import org.vstu.meaningtree.nodes.expressions.logical.ShortCircuitOrOp;
import org.vstu.meaningtree.nodes.expressions.math.*;
import org.vstu.meaningtree.nodes.expressions.unary.*;
import org.vstu.meaningtree.nodes.statements.CompoundStatement;
import org.vstu.meaningtree.nodes.statements.assignments.AssignmentStatement;
import org.vstu.meaningtree.nodes.statements.conditions.IfStatement;
import org.vstu.meaningtree.nodes.types.builtin.*;

import java.util.HashMap;
import java.util.Map;

public class AugletsSerializer {
    private int _variableNumber = 0;
    private final Map<String, String> _variableMapping = new HashMap<>();
    private int _typeNumber = 0;
    private final Map<String, String> _typeMapping = new HashMap<>();
    private final Circumflexer _circumflexer = new Circumflexer();

    public String serialize(AugletProblem problem) {
        _circumflexer.setProblem(problem);
        _circumflexer.problemMode();
        var problemStr = toString(
                problem.problemMeaningTree().getRootNode()
        );

        _circumflexer.solutionMode();
        var solutionStr = toString(
                problem.solutionMeaningTree().getRootNode()
        );

        _variableMapping.clear();
        _typeMapping.clear();

        return problemStr + "\n solution: \n" + solutionStr;
    }

    private String toString(Node node) {
        var nodeStr = dispatchNode(node);

        return _circumflexer.circumflexifyUnique(nodeStr, node);
    }

    private String dispatchNode(Node node) {
        System.out.println("dispatchNode: " + node.getClass().getSimpleName());

        return switch (node) {
            case VariableDeclaration varDecl -> toString(varDecl);
            case ProgramEntryPoint entryPoint -> toString(entryPoint);
            case IfStatement ifStatement -> toString(ifStatement);
            case SimpleIdentifier simpleIdentifier -> toString(simpleIdentifier);
            case NumericLiteral numericLiteral -> toString(numericLiteral);
            case CompoundStatement compoundStatement -> toString(compoundStatement);
            case AssignmentStatement assignmentStatement -> toString(assignmentStatement);
            case BinaryExpression expr -> toString(expr);
            case UnaryExpression expr -> toString(expr);
            default -> throw new IllegalStateException(String.format("Can't stringify node %s", node.getClass().getSimpleName()));
        };
    }

    private String toString(ProgramEntryPoint entryPoint) {
        var entryPointBuilder = new StringBuilder();

        entryPointBuilder.append("<V0><T0>{");

        for (var stmt : entryPoint.getBody()) {
            entryPointBuilder.append(toString(stmt));
        }

        entryPointBuilder.append("}");
        return entryPointBuilder.toString();
    }

    private String toString(IfStatement ifStatement) {
        var ifStatementBuilder = new StringBuilder();

        var branches = ifStatement.getBranches();
        var ifBranch = branches.getFirst();

        ifStatementBuilder
                .append("if(")
                .append(toString(ifBranch.getCondition()))
                .append("){")
                .append(toString(ifBranch.getBody()))
                .append("}");

        for (var elseIfBranch : branches.subList(1, branches.size())) {
            ifStatementBuilder
                    .append("else if (")
                    .append(toString(elseIfBranch.getCondition()))
                    .append("){")
                    .append(toString(elseIfBranch.getBody()))
                    .append("}");
        }

        if (ifStatement.hasElseBranch()) {
            var elseBranch = ifStatement.getElseBranch();
            ifStatementBuilder
                    .append("else {")
                    .append(toString(elseBranch))
                    .append("}");
        }

        return ifStatementBuilder.toString();
    }

    private String toString(SimpleIdentifier identifier) {
        return variableIdentify(identifier);
    }

    private String toString(VariableDeclaration varDecl) {
        if (varDecl.getDeclarators().length != 1) {
            throw new IllegalStateException(String.format("Can't process node %s. Multiple declaration isn't supported", varDecl.getClass()));
        }

        var decl = varDecl.getDeclarators()[0];

        var varDeclBuilder = new StringBuilder();

        varDeclBuilder
                .append(typeIdentify(varDecl.getType()))
                .append(variableIdentify(decl.getIdentifier()));

        if (decl.hasInitialization()) {
            var rvalue = decl.getRValue();
            varDeclBuilder.append("=").append(toString(rvalue));
        }

        return varDeclBuilder.toString();
    }

    private String toString(NumericLiteral literal) {
        return literal.getStringValue(false);
    }

    private String toString(CompoundStatement compoundStatement) {
        var compoundStatementBuilder = new StringBuilder();

        for (var stmt : compoundStatement.getNodes()) {
            compoundStatementBuilder.append(toString(stmt)).append(";");
        }

        return compoundStatementBuilder.toString();
    }

    private String toString(AssignmentStatement assignmentStatement) {
        return toString(assignmentStatement.getLValue()) + "=" + toString(assignmentStatement.getRValue());
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

    private String typeIdentify(Type type) {
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

    private String variableIdentify(SimpleIdentifier identifier) {
        var varName = identifier.toString();

        if (_variableMapping.containsKey(varName)) {
            return _variableMapping.get(varName);
        }

        var res =  "<V" + _variableNumber + "#" + varName + "#>";
        _variableNumber++;
        _variableMapping.put(varName, res);

        return res;
    }

}
