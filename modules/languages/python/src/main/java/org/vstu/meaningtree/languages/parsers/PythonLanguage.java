package org.vstu.meaningtree.languages.parsers;

import org.treesitter.*;
import org.vstu.meaningtree.MeaningTree;
import org.vstu.meaningtree.ParenthesizedExpression;
import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Identifier;
import org.vstu.meaningtree.nodes.Node;
import org.vstu.meaningtree.nodes.bitwise.*;
import org.vstu.meaningtree.nodes.literals.FloatLiteral;
import org.vstu.meaningtree.nodes.literals.IntegerLiteral;
import org.vstu.meaningtree.nodes.logical.NotOp;
import org.vstu.meaningtree.nodes.math.*;
import org.vstu.meaningtree.nodes.statements.ConditionStatement;
import org.vstu.meaningtree.nodes.statements.CompoundStatement;
import org.vstu.meaningtree.nodes.statements.ElseStatement;
import org.vstu.meaningtree.nodes.statements.IfStatement;
import org.vstu.meaningtree.nodes.unary.UnaryMinusOp;
import org.vstu.meaningtree.nodes.unary.UnaryPlusOp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class PythonLanguage extends Language {
    /*
    TODO:
     - assignment
     - string, list, dict, set, tuple literals
     - getCodePiece to toString??
     - comparison
     - if statement finish
     - unary plus, minus
     */

    @Override
    public MeaningTree getMeaningTree(String code) {
        _code = code;
        TSParser parser = new TSParser();
        TSLanguage javaLanguage = new TreeSitterPython();

        parser.setLanguage(javaLanguage);
        TSTree tree = parser.parseString(null, code);
        try {
            tree.printDotGraphs(new File("TSTree.dot"));
        } catch (IOException e) { }

        return new MeaningTree(fromTSNode(tree.getRootNode()));
    }

    private Node fromTSNode(TSNode node) {
        if (node.isNull()) {
            return null;
        }
        String nodeType = node.getType();
        return switch (nodeType) {
            case "module", "block" -> fromCompoundTSNode(node);
            case "if_statement" -> fromIfStatementTSNode(node, null);
            case "expression_statement" -> fromExpressionStatementTSNode(node);
            case "parenthesized_expression" -> fromParenthesizedExpressionTSNode(node);
            case "binary_operator" -> fromBinaryExpressionTSNode(node);
            case "unary_expression" -> fromUnaryExpressionTSNode(node);
            case "not_operator" -> fromNotOperatorTSNode(node);
            case "pass_statement" -> null;
            case "integer" -> fromIntegerLiteralTSNode(node);
            case "float" -> fromFloatLiteralTSNode(node);
            case "identifier" -> fromIdentifier(node);
            case null, default -> throw new UnsupportedOperationException(String.format("Can't parse %s", node.getType()));
        };
    }

    private Node fromIdentifier(TSNode node) {
        return new Identifier(node.toString());
    }

    private CompoundStatement fromCompoundTSNode(TSNode node) {
        ArrayList<Node> nodes = new ArrayList<>();
        for (int i = 0; i < node.getChildCount(); i++) {
            nodes.add(fromTSNode(node.getChild(i)));
        }
        return new CompoundStatement(nodes.toArray(new Node[0]));
    }

    private ConditionStatement fromIfStatementTSNode(TSNode node, TSNode alternativeNode) {
        if (node.getType().equals("else_clause")) {
            return fromElseStatementTSNode(node);
        }
        Expression condition = (Expression) fromTSNode(node.getChildByFieldName("condition"));
        CompoundStatement consequence = (CompoundStatement) fromTSNode(node.getChildByFieldName("consequence"));
        TSNode altNode = node.getChildByFieldName("alternative");
        ConditionStatement alternative = null;
        if (alternativeNode == null && altNode != null) {
            alternative = fromIfStatementTSNode(altNode, altNode.getNextNamedSibling());
        } else if (altNode.isNull() && alternativeNode != null) {
            //TODO: fix hardcoded "null"
            alternative = fromIfStatementTSNode(alternativeNode, alternativeNode.getNextNamedSibling());
        }
        return new IfStatement(condition, consequence, alternative);
    }

    private ConditionStatement fromElseStatementTSNode(TSNode node) {
        CompoundStatement consequence = (CompoundStatement) fromTSNode(node.getChildByFieldName("block"));
        return new ElseStatement(consequence);
    }

    private Node fromUnaryExpressionTSNode(TSNode node) {
        Expression argument = (Expression) fromTSNode(node.getChildByFieldName("operand"));
        TSNode operation = node.getChildByFieldName("operator");
        return switch (getCodePiece(operation)) {
            case "~" -> new InversionOp(argument);
            case "-" -> new UnaryMinusOp(argument);
            case "+" -> new UnaryPlusOp(argument);
            case null, default -> throw new UnsupportedOperationException();
        };
    }

    private Node fromNotOperatorTSNode(TSNode node) {
        Expression argument = (Expression) fromTSNode(node.getChildByFieldName("argument"));
        return new NotOp(argument);
    }


    private Node fromExpressionStatementTSNode(TSNode node) {
        return fromTSNode(node.getChild(0));
    }

    private Node fromParenthesizedExpressionTSNode(TSNode node) {
        return new ParenthesizedExpression((Expression) fromTSNode(node.getChild(0)));
    }

    private Node fromIntegerLiteralTSNode(TSNode node) {
        String value = getCodePiece(node);
        return new IntegerLiteral(value);
    }

    private Node fromFloatLiteralTSNode(TSNode node) {
        String value = getCodePiece(node);
        return new FloatLiteral(value);
    }

    private Node fromBinaryExpressionTSNode(TSNode node) {
        Expression left = (Expression) fromTSNode(node.getChildByFieldName("left"));
        Expression right = (Expression) fromTSNode(node.getChildByFieldName("right"));
        TSNode operator = node.getChildByFieldName("operator");

        return switch (getCodePiece(operator)) {
            case "+" -> new AddOp(left, right);
            case "-" -> new SubOp(left, right);
            case "*" -> new MulOp(left, right);
            case "/" -> new DivOp(left, right);
            case "//" -> new FloorDivOp(left, right);
            case "%" -> new ModOp(left, right);
            case "<<" -> new LeftShiftOp(left, right);
            case ">>" -> new RightShiftOp(left, right);
            case "&" -> new BitwiseAndOp(left, right);
            case "|" -> new BitwiseOrOp(left, right);
            case "^" -> new XorOp(left, right);
            default -> throw new UnsupportedOperationException(String.format("Can't parse operator %s", getCodePiece(operator)));
        };
    }
}
