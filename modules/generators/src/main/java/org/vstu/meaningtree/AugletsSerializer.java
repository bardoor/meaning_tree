package org.vstu.meaningtree;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Node;
import org.vstu.meaningtree.nodes.Type;
import org.vstu.meaningtree.nodes.declarations.VariableDeclaration;
import org.vstu.meaningtree.nodes.expressions.identifiers.SimpleIdentifier;
import org.vstu.meaningtree.nodes.types.builtin.*;

import java.util.List;

public class AugletsSerializer {
    private AugletsMeta _meta;
    private int _variableNumber = 0;
    private int _typeNumber = 0;

    public String serialize(AugletProblem problem) {
        if (_meta == null) {
            throw new IllegalStateException("Meta was not set. Please call AugletsSerializer.setMeta() first");
        }

        var problemStr = toString(problem.problemMeaningTree().getRootNode(), problem.meta().uniqueProblemNodes());
        var solutionStr = toString(problem.solutionMeaningTree().getRootNode(), problem.meta().uniqueSolutionNodes());

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

    private String type(Type type) {
        var typeName = switch (type) {
            case IntType t -> "int";
            case StringType t -> "string";
            case FloatType t -> "float";
            case BooleanType t -> "boolean";
            case CharacterType t -> "char";
            default -> throw new IllegalStateException(String.format("Type %s is not supported", type.getClass()));
        };

        var res =  "<T" + _typeNumber + "#" + typeName + "#>";
        _typeNumber++;

        return res;
    }

    private String variable(SimpleIdentifier identifier) {
        var res =  "<" + _variableNumber + "#" + identifier.toString() + "#>";
        _variableNumber++;
        return res;
    }

}
