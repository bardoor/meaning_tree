package org.vstu.meaningtree.languages.parsers;

import org.treesitter.*;
import org.vstu.meaningtree.MeaningTree;
import org.vstu.meaningtree.nodes.*;
import org.vstu.meaningtree.nodes.bitwise.*;
import org.vstu.meaningtree.nodes.comparison.*;
import org.vstu.meaningtree.nodes.identifiers.SimpleIdentifier;
import org.vstu.meaningtree.nodes.literals.*;
import org.vstu.meaningtree.nodes.logical.NotOp;
import org.vstu.meaningtree.nodes.math.*;
import org.vstu.meaningtree.nodes.statements.AssignmentStatement;
import org.vstu.meaningtree.nodes.statements.CompoundStatement;
import org.vstu.meaningtree.nodes.statements.ConditionBranch;
import org.vstu.meaningtree.nodes.statements.IfStatement;
import org.vstu.meaningtree.nodes.unary.UnaryMinusOp;
import org.vstu.meaningtree.nodes.unary.UnaryPlusOp;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.regex.Pattern;

public class PythonLanguage extends Language {
    /*
    TODO:
     - support entry point
     - support functions and methods with decorators and type annotations
     - function calls and object initalization
     - variable declarations with type annotations
     - break, continue, return
     - parse member access and indexing
     - support slicing
     - boolean operators
     - for, for-each
     - while
     - ternary operator
     - dict literal
     - classes support
     - import
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
            case "if_statement" -> fromIfStatementTSNode(node);
            case "expression_statement" -> fromExpressionStatementTSNode(node);
            case "parenthesized_expression" -> fromParenthesizedExpressionTSNode(node);
            case "binary_operator" -> fromBinaryExpressionTSNode(node);
            case "unary_expression" -> fromUnaryExpressionTSNode(node);
            case "not_operator" -> fromNotOperatorTSNode(node);
            case "pass_statement" -> null;
            case "integer" -> fromIntegerLiteralTSNode(node);
            case "float" -> fromFloatLiteralTSNode(node);
            case "identifier" -> fromIdentifier(node);
            case "comparison_operator" -> fromComparisonTSNode(node);
            case "list", "set", "tuple" -> fromList(node, node.getType());
            case "string" -> fromString(node);
            case "comment" -> fromComment(node);
            case "none" -> new NullLiteral();
            case "assignment", "named_expression" -> fromAssignmentTSNode(node);
            case null, default -> throw new UnsupportedOperationException(String.format("Can't parse %s", node.getType()));
        };
    }

    private Node createEntryPoint(TSNode node) {
        // detect if __name__ == __main__ construction
        Pattern pattern = Pattern.compile("__name__\\s+==\\s['\\\"]__main__['\\\"]");
        for (int i = 0; i < node.getNamedChildCount(); i++) {
            TSNode children = node.getChild(i);
            if (children.getType().equals("if_statement")) {
                if (pattern.matcher(getCodePiece(children.getChildByFieldName("condition"))).hasMatch()) {
                    TSNode body = children.getChildByFieldName("consequence");
                    if (body.getNamedChildCount() == 1 && body.getNamedChild(0).getNamedChild(0).getType().equals("call")) {
                        String functionName = getCodePiece(body.getNamedChild(0).getNamedChild(0).getChildByFieldName("function"));
                        // TODO: check function availability, else wrap all content
                    } else {
                        //TODO: wrap all content to created by tree main function
                    }
                }
            }
        }
        // TODO: if main function isn't detected: wrap all content to body
        return null;
    }

    private Node fromIdentifier(TSNode node) {
        return new SimpleIdentifier(getCodePiece(node));
    }

    private Node fromString(TSNode node) {
        TSNode content = node.getChildByFieldName("string_content");
        //TODO: hardcode output in viewer? What about escaping and ", ', """
        if (node.getChildByFieldName("string_start").getType().equals("\"\"\"")
                && node.getParent().getType().equals("expression_statement")) {
            return new Comment(getCodePiece(content));
        }
        return new StringLiteral(getCodePiece(content));
    }

    private Node fromComment(TSNode node) {
        return new Comment(getCodePiece(node).replace("#", "").trim());
    }

    private Node fromAssignmentTSNode(TSNode node) {
        TSNode operator = node.getChildByFieldName("left").getNextSibling();
        Expression left = (Expression) fromTSNode(node.getChildByFieldName("left"));
        Expression right = (Expression) fromTSNode(node.getChildByFieldName("right"));
        if (getCodePiece(operator).equals("=")) {
            return new AssignmentStatement(left, right);
        } else if (getCodePiece(operator).equals(":=")){
            return new AssignmentExpression(left, right);
        } else {
            AugmentedAssignmentOperator augOp;
            switch (getCodePiece(operator)) {
                case "+=":
                    augOp = AugmentedAssignmentOperator.ADD;
                    break;
                case "-=":
                    augOp = AugmentedAssignmentOperator.SUB;
                    break;
                case "*=":
                    augOp = AugmentedAssignmentOperator.MUL;
                    break;
                case "/=":
                    augOp = AugmentedAssignmentOperator.DIV;
                    break;
                case "//=":
                    augOp = AugmentedAssignmentOperator.FLOORDIV;
                    break;
                case "|=":
                    augOp = AugmentedAssignmentOperator.BITWISE_OR;
                    break;
                case "&=":
                    augOp = AugmentedAssignmentOperator.BITWISE_AND;
                    break;
                case ">>=":
                    augOp = AugmentedAssignmentOperator.BITWISE_SHIFT_RIGHT;
                    break;
                case "<<=":
                    augOp = AugmentedAssignmentOperator.BITWISE_SHIFT_LEFT;
                    break;
                case "%=":
                    augOp = AugmentedAssignmentOperator.MOD;
                    break;
                case "**=":
                    augOp = AugmentedAssignmentOperator.POW;
                    break;
                case "^=":
                    augOp = AugmentedAssignmentOperator.BITWISE_XOR;
                    break;
                default:
                    augOp = AugmentedAssignmentOperator.NONE;
            }
            return new AssignmentStatement(left, right, augOp);
        }
    }

    private Node fromList(TSNode node, String type) {
        List<Expression> exprs = new ArrayList<>();
        for (int i = 0; i < node.getNamedChildCount(); i++) {
            Expression expr = (Expression) fromTSNode(node.getChild(i));
            exprs.add(expr);
        }
        return switch (type) {
            case "list" -> new ListLiteral(exprs.toArray(new Expression[0]));
            case "tuple" -> new UnmodifiableListLiteral(exprs.toArray(new Expression[0]));
            case "set" -> new SetLiteral(exprs.toArray(new Expression[0]));
            default -> null;
        };
    }

    private CompoundStatement fromCompoundTSNode(TSNode node) {
        ArrayList<Node> nodes = new ArrayList<>();
        for (int i = 0; i < node.getChildCount(); i++) {
            nodes.add(fromTSNode(node.getChild(i)));
        }
        return new CompoundStatement(nodes.toArray(new Node[0]));
    }

    private IfStatement fromIfStatementTSNode(TSNode node) {
        List<ConditionBranch> branches = new ArrayList<>();
        branches.add(createConditionBranchTSNode(node));
        Statement elseBranch = null;
        TSNode altNode = node.getChildByFieldName("alternative");
        while (!altNode.isNull() &&
                (altNode.getType().equals("elif_clause") || altNode.getType().equals("else_clause"))) {
            if (altNode.getType().equals("elif_clause")) {
                branches.add(createConditionBranchTSNode(altNode));
            } else {
                elseBranch = (Statement) fromTSNode(altNode.getChildByFieldName("block"));
            }
            altNode = altNode.getNextNamedSibling();
        }
        return new IfStatement(branches, elseBranch);
    }

    private ConditionBranch createConditionBranchTSNode(TSNode node) {
        Expression condition = (Expression) fromTSNode(node.getChildByFieldName("condition"));
        CompoundStatement consequence = (CompoundStatement) fromTSNode(node.getChildByFieldName("consequence"));
        return new ConditionBranch(condition, consequence);
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

    private Node fromComparisonTSNode(TSNode node) {
        Class<? extends BinaryComparison> operator = null;
        ArrayDeque<TSNode> operands = new ArrayDeque<>();

        List<BinaryComparison> comparisons = new ArrayList<>();

        for (int i = 0; i < node.getChildCount(); i++) {
            if (operands.size() == 2) {
                Expression firstOp = (Expression) fromTSNode(operands.removeFirst());
                TSNode secondNode = operands.removeFirst();
                Expression secondOp = (Expression) fromTSNode(secondNode);
                try {
                    BinaryComparison object = operator.getDeclaredConstructor(Expression.class, Expression.class).newInstance(firstOp, secondOp);
                    comparisons.add(object);
                } catch (InstantiationException | InvocationTargetException | IllegalAccessException |
                         NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
                operands.add(secondNode);
            }
            TSNode children = node.getChild(i);

            switch (children.getType()) {
                case "<":
                    operator = LtOp.class;
                    break;
                case ">":
                    operator = GtOp.class;
                    break;
                case "<=":
                    operator = LeOp.class;
                    break;
                case ">=":
                    operator = GeOp.class;
                    break;
                case "==":
                    operator = EqOp.class;
                    break;
                case "!=":
                    operator = NotEqOp.class;
                    break;
                default:
                    operands.add(children);
            }
        }
        if (operands.size() == 2) {
            Expression firstOp = (Expression) fromTSNode(operands.removeFirst());
            TSNode secondNode = operands.removeFirst();
            Expression secondOp = (Expression) fromTSNode(secondNode);
            try {
                BinaryComparison object = operator.getDeclaredConstructor(Expression.class, Expression.class).newInstance(firstOp, secondOp);
                comparisons.add(object);
            } catch (InstantiationException | InvocationTargetException | IllegalAccessException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        return new CompoundComparison(comparisons.toArray(new BinaryComparison[0]));
    }

    private Node fromBinaryExpressionTSNode(TSNode node) {
        Expression left = (Expression) fromTSNode(node.getChildByFieldName("left"));
        Expression right = (Expression) fromTSNode(node.getChildByFieldName("right"));
        TSNode operator = node.getChildByFieldName("operator");

        return switch (getCodePiece(operator)) {
            case "+" -> new AddOp(left, right);
            case "-" -> new SubOp(left, right);
            case "*" -> new MulOp(left, right);
            case "**" -> new PowOp(left, right);
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