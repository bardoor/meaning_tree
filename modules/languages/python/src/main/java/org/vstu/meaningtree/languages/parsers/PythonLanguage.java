package org.vstu.meaningtree.languages.parsers;

import org.treesitter.*;
import org.vstu.meaningtree.MeaningTree;
import org.vstu.meaningtree.nodes.*;
import org.vstu.meaningtree.nodes.bitwise.*;
import org.vstu.meaningtree.nodes.comparison.*;
import org.vstu.meaningtree.nodes.declarations.VariableDeclaration;
import org.vstu.meaningtree.nodes.identifiers.ScopedIdentifier;
import org.vstu.meaningtree.nodes.identifiers.SimpleIdentifier;
import org.vstu.meaningtree.nodes.literals.*;
import org.vstu.meaningtree.nodes.logical.NotOp;
import org.vstu.meaningtree.nodes.logical.ShortCircuitAndOp;
import org.vstu.meaningtree.nodes.logical.ShortCircuitOrOp;
import org.vstu.meaningtree.nodes.math.*;
import org.vstu.meaningtree.nodes.statements.*;
import org.vstu.meaningtree.nodes.types.*;
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
     - support functions and methods with decorators and type annotations
     - function calls and object initalization
     - support entry point
     - for, for-each
     - while
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
            case "dictionary" -> fromDictionary(node);
            case "string" -> fromString(node);
            case "slice" -> fromSlice(node);
            case "comment" -> fromComment(node);
            case "boolean_operator" -> fromBooleanOperatorTSNode(node);
            case "none" -> new NullLiteral();
            case "wildcard_import" -> ScopedIdentifier.ALL;
            case "break_statement" -> new BreakStatement();
            case "continue_statement" -> new ContinueStatement();
            case "subscript" -> fromIndexTSNode(node);
            case "dotted_name" -> fromDottedNameTSNode(node);
            case "attribute"-> fromAttributeTSNode(node);
            case "return_statement" -> fromReturnTSNode(node);
            case "conditional_expresstion"-> fromTernaryOperatorTSNode(node);
            case "assignment", "named_expression" -> fromAssignmentTSNode(node);
            case null, default -> throw new UnsupportedOperationException(String.format("Can't parse %s", node.getType()));
        };
    }

    private Node fromIndexTSNode(TSNode node) {
        Expression base = (Expression) fromTSNode(node.getChildByFieldName("value"));
        Expression index = (Expression) fromTSNode(node.getChildByFieldName("subscript"));
        return new IndexExpression(base, index);
    }

    private Node fromAttributeTSNode(TSNode node) {
        Expression expr = (Expression) fromTSNode(node.getChildByFieldName("object"));
        SimpleIdentifier member = (SimpleIdentifier) fromTSNode(node.getChildByFieldName("attribute"));
        return new MemberAccess(expr, member);
    }

    private Node fromDictionary(TSNode node) {
        SortedMap<Expression, Expression> dict = new TreeMap<>();
        for (int i = 0; i < node.getNamedChildCount(); i++) {
            Expression key = (Expression) fromTSNode(node.getChild(i).getChildByFieldName("key"));
            Expression value = (Expression) fromTSNode(node.getChild(i).getChildByFieldName("value"));
            dict.put(key, value);
        }
        return new DictionaryLiteral(dict);
    }

    private Node fromTernaryOperatorTSNode(TSNode node) {
        Expression thenExpr = (Expression) fromTSNode(node.getNamedChild(0));
        Expression ifCond = (Expression) fromTSNode(node.getNamedChild(1));
        Expression elseExpr = (Expression) fromTSNode(node.getNamedChild(2));
        return new TernaryOperator(ifCond, thenExpr, elseExpr);
    }

    private Node fromDottedNameTSNode(TSNode node) {
        List<SimpleIdentifier> members = new ArrayList<>();
        for (int i = 0; i < node.getNamedChildCount(); i++) {
            members.add((SimpleIdentifier) fromTSNode(node.getNamedChild(i)));
        }
        return new ScopedIdentifier(members.toArray(new SimpleIdentifier[0]));
    }

    private Node fromSlice(TSNode node) {
        Expression start = null;
        Expression stop = null;
        Expression step = null;

        int stage = 0;
        for (int i = 0; i < node.getChildCount(); i++) {
            if (node.getChild(i).getType().equals(":")) {
                stage += 1;
            } else {
                switch (stage) {
                    case 0:
                        start = (Expression) fromTSNode(node.getChild(i));
                        break;
                    case 1:
                        stop = (Expression) fromTSNode(node.getChild(i));
                        break;
                    case 2:
                        step = (Expression) fromTSNode(node.getChild(i));
                        break;
                }
            }
        }
        return new Range(start, stop, step);
    }

    private Node fromReturnTSNode(TSNode node) {
        if (node.getNamedChildCount() > 0) {
            return new ReturnStatement((Expression) fromTSNode(node.getNamedChild(0)));
        }
        return new ReturnStatement(null);
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

    private Type determineType(TSNode typeNode) {
        if (typeNode.getNamedChildCount() > 0 && typeNode.getNamedChild(0).getType().equals("generic_type")) {
            TSNode genericTypeNode = typeNode.getNamedChild(0);
            List<Type> genericTypes = new ArrayList<>();
            String typeName = getCodePiece(typeNode.getNamedChild(0));
            for (int i = 0; i < genericTypeNode.getNamedChildCount(); i++) {
                genericTypes.add(determineType(genericTypeNode.getNamedChild(i)));
            }
            switch (typeName) {
                case "list":
                    return new ListType(genericTypes.getFirst());
                case "tuple":
                    return new UnmodifiableListType(genericTypes.getFirst());
                case "dict":
                    return new DictionaryType(genericTypes.getFirst(), genericTypes.get(1));
                default:
                    return new GenericUserType(new SimpleIdentifier(typeName), genericTypes.toArray(new Type[0]));
            }
        }
        switch (getCodePiece(typeNode)) {
            case "str":
                return new StringType();
            case "int":
                return new IntType();
            case "float":
                return new FloatType();
            case "list":
                return new ListType(new UnknownType());
            case "tuple":
                return new UnmodifiableListType(new UnknownType());
            case "dict":
                return new DictionaryType(new UnknownType(), new UnknownType());
            case "set":
                return new SetType(new UnknownType());
            default:
                return new UserType(new SimpleIdentifier(getCodePiece(typeNode)));
        }
    }

    private Node fromAssignmentTSNode(TSNode node) {
        TSNode operator = node.getChildByFieldName("left").getNextSibling();
        Expression left = (Expression) fromTSNode(node.getChildByFieldName("left"));
        Expression right = (Expression) fromTSNode(node.getChildByFieldName("right"));

        if (!node.getChildByFieldName("type").isNull()) {
            Type type = determineType(node.getChildByFieldName("type"));
            return new VariableDeclaration(type, (SimpleIdentifier) left, right);
        }

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

    private Node fromBooleanOperatorTSNode(TSNode node) {
        Expression left = (Expression) fromTSNode(node.getChildByFieldName("left"));
        Expression right = (Expression) fromTSNode(node.getChildByFieldName("right"));
        TSNode operator = node.getChildByFieldName("operator");

        if (getCodePiece(operator).equals("and")) {
            return new ShortCircuitAndOp(left, right);
        } else if (getCodePiece(operator).equals("or")) {
            return new ShortCircuitOrOp(left, right);
        }
        return null;
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