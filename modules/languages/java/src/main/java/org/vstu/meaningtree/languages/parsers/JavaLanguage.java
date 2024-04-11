package org.vstu.meaningtree.languages.parsers;
import org.treesitter.*;
import org.vstu.meaningtree.MeaningTree;
import org.vstu.meaningtree.nodes.ParenthesizedExpression;
import org.vstu.meaningtree.nodes.Type;
import org.vstu.meaningtree.nodes.*;
import org.vstu.meaningtree.nodes.declarations.VariableDeclaration;
import org.vstu.meaningtree.nodes.statements.CompoundStatement;
import org.vstu.meaningtree.nodes.statements.*;
import org.vstu.meaningtree.nodes.comparison.*;
import org.vstu.meaningtree.nodes.literals.FloatLiteral;
import org.vstu.meaningtree.nodes.literals.IntegerLiteral;
import org.vstu.meaningtree.nodes.logical.NotOp;
import org.vstu.meaningtree.nodes.math.AddOp;
import org.vstu.meaningtree.nodes.math.DivOp;
import org.vstu.meaningtree.nodes.math.MulOp;
import org.vstu.meaningtree.nodes.math.SubOp;
import org.vstu.meaningtree.nodes.types.FloatType;
import org.vstu.meaningtree.nodes.types.IntType;

import java.io.File;
import java.io.IOException;

public class JavaLanguage extends Language {
    public MeaningTree getMeaningTree(String code) {
        _code = code;
        TSParser parser = new TSParser();
        TSLanguage javaLanguage = new TreeSitterJava();

        parser.setLanguage(javaLanguage);
        TSTree tree = parser.parseString(null, code);
        try {
            tree.printDotGraphs(new File("TSTree.dot"));
        } catch (IOException e) { }

        return new MeaningTree(fromTSNode(tree.getRootNode()));
    }

    private Node fromTSNode(TSNode node) {
        String nodeType;
        try {
            nodeType = node.getType();
        } catch (TSException e) {
            return null;
        }
        return switch (nodeType) {
            case "program" -> fromProgramTSNode(node);
            case "block" -> fromBlockTSNode(node);
            case "statement" -> fromStatementTSNode(node);
            case "if_statement" -> fromIfStatementTSNode(node);
            case "condition" -> fromConditionTSNode(node);
            case "expression_statement" -> fromExpressionStatementTSNode(node);
            case "parenthesized_expression" -> fromParenthesizedExpressionTSNode(node);
            case "binary_expression" -> fromBinaryExpressionTSNode(node);
            case "unary_expression" -> fromUnaryExpressionTSNode(node);
            case "decimal_integer_literal" -> fromIntegerLiteralTSNode(node);
            case "decimal_floating_point_literal" -> fromFloatLiteralTSNode(node);
            case "local_variable_declaration" -> fromVariableDeclarationTSNode(node);
            case "for_statement" -> fromForStatementTSNode(node);
            case "assignment_expression" -> fromAssignmentExpressionTSNode(node);
            case "identifier" -> fromIdentifierTSNode(node);
            case null, default -> throw new UnsupportedOperationException(String.format("Can't parse %s", node.getType()));
        };
    }

    private Node fromIdentifierTSNode(TSNode node) {
        String variableName = getCodePiece(node);
        return new Identifier(variableName);
    }

    private Node fromAssignmentExpressionTSNode(TSNode node) {
        String variableName = getCodePiece(node.getChildByFieldName("left"));
        Identifier identifier = new Identifier(variableName);
        Expression right = (Expression) fromTSNode(node.getChildByFieldName("right"));
        return new AssignmentExpression(identifier, right);
    }

    private Node fromForStatementTSNode(TSNode node) {
        CanInitialize init = null;
        Expression condition = null;
        Expression update = null;

        if (!node.getChildByFieldName("init").isNull()) {
            init = (CanInitialize) fromTSNode(node.getChildByFieldName("init"));
        }

        if (!node.getChildByFieldName("condition").isNull()) {
            condition = (Expression) fromTSNode(node.getChildByFieldName("condition"));
        }

        if (!node.getChildByFieldName("update").isNull()) {
            update = (Expression) fromTSNode(node.getChildByFieldName("update"));
        }

        CompoundStatement body = (CompoundStatement) fromTSNode(node.getChildByFieldName("body"));

        return new GeneralForLoop(init, condition, update, body);
    }

    private Node fromVariableDeclarationTSNode(TSNode node) {
        String typeName = getCodePiece(node.getChildByFieldName("type"));

        Type type = switch (typeName) {
            case "int", "short", "long" -> new IntType();
            case "float", "double" -> new FloatType();
            default -> throw new IllegalStateException("Unexpected value: " + typeName);
        };

        TSNode declarator = node.getChildByFieldName("declarator");

        String variableName = getCodePiece(declarator.getChildByFieldName("name"));
        Identifier identifier = new Identifier(variableName);

        if (!declarator.getChildByFieldName("value").isNull()) {
            Expression value = (Expression) fromTSNode(declarator.getChildByFieldName("value"));
            return new VariableDeclaration(type, identifier, value);
        }

        return new VariableDeclaration(type, identifier);
    }

    private Node fromProgramTSNode(TSNode node) {
        return fromTSNode(node.getChild(0));
    }

    private Node fromBlockTSNode(TSNode node) {
        CompoundStatement compoundStatement = new CompoundStatement();
        for (int i = 1; i < node.getChildCount() - 1; i++) {
            Node child = fromTSNode(node.getChild(i));
            compoundStatement.add(child);
        }
        return compoundStatement;
    }

    private Node fromStatementTSNode(TSNode node) {
        return fromTSNode(node.getChild(0));
    }

    private Node fromIfStatementTSNode(TSNode node) {
        Expression condition = (Expression) fromTSNode(node.getChildByFieldName("condition"));
        CompoundStatement consequence = (CompoundStatement) fromTSNode(node.getChildByFieldName("consequence"));
        CompoundStatement alternative = (CompoundStatement) fromTSNode(node.getChildByFieldName("alternative"));
        return new IfStatement(condition, consequence, alternative);
    }

    private Node fromConditionTSNode(TSNode node) {
        // TODO: Что-то сделать с этим...
        // У condition дети: '(', 'binary_expression', ')'
        // По имени binary_expression почему-то получить не удалось
        return fromTSNode(node.getChild(1));
    }

    private Node fromExpressionStatementTSNode(TSNode node) {
        Expression expr = (Expression) fromTSNode(node.getChild(0));
        return new ExpressionStatement(expr);
    }

    private Node fromParenthesizedExpressionTSNode(TSNode node) {
        Expression expr = (Expression) fromTSNode(node.getChild(1));
        return new ParenthesizedExpression(expr);
    }

    private Node fromIntegerLiteralTSNode(TSNode node) {
        String value = getCodePiece(node);
        return new IntegerLiteral(value);
    }

    private Node fromFloatLiteralTSNode(TSNode node) {
        String value = getCodePiece(node);
        return new FloatLiteral(value);
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
            case "!=" -> new NotEqOp(left, right);
            case ">=" -> new GeOp(left, right);
            case "<=" -> new LeOp(left, right);
            default -> throw new UnsupportedOperationException(String.format("Can't parse operator %s", getCodePiece(operator)));
        };
    }


}
