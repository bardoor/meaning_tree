package languages.parsers;
import meaning_tree.*;
import org.treesitter.*;

import java.util.SplittableRandom;

public class JavaLanguage extends Language {
    public MeaningTree getMeaningTree(String code) {
        TSParser parser = new TSParser();
        TSLanguage javaLanguage = new TreeSitterJava();

        parser.setLanguage(javaLanguage);
        TSTree tree = parser.parseString(null, code);

        return new MeaningTree(fromTSNode(tree.getRootNode()));
    }

    private Node fromTSNode(TSNode node) {
        return switch (node.getType()) {
            case "program" -> fromProgramTSNode(node);
            case "expression_statement" -> fromExpressionStatementTSNode(node);
            case "parenthesized_expression" -> fromParenthesizedExpressionTSNode(node);
            case "binary_expression" -> fromBinaryExpressionTSNode(node);
            case "unary_expression" -> fromUnaryExpressionTSNode(node);
            case null, default -> throw new UnsupportedOperationException();
        };
    }

    private Node fromProgramTSNode(TSNode node) {
        return fromTSNode(node.getChild(0));
    }

    private Node fromExpressionStatementTSNode(TSNode node) {
        return fromTSNode(node.getChild(0));
    }

    private Node fromParenthesizedExpressionTSNode(TSNode node) {
        return fromTSNode(node.getChild(0));
    }

    private Node fromUnaryExpressionTSNode(TSNode node) {
        Expression argument = (Expression) fromTSNode(node.getChildByFieldName("operand"));
        TSNode operation = node.getChildByFieldName("operator");
        return switch (getCodePiece(operation)) {
            case "!" -> new NotOp(argument);
            case null, default -> throw new UnsupportedOperationException();
        };
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
            case "<" -> new LtOp(left, right);
            case ">" -> new GtOp(left, right);
            case "==" -> new EqOp(left, right);
            case "!=" -> new NeOp(left, right);
            case ">=" -> new GeOp(left, right);
            case "<=" -> new LeOp(left, right);
            default -> throw new UnsupportedOperationException();
        };
    }


}
