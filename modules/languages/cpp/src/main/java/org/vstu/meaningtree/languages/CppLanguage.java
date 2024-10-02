package org.vstu.meaningtree.languages;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.treesitter.*;
import org.vstu.meaningtree.MeaningTree;
import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Node;
import org.vstu.meaningtree.nodes.Type;
import org.vstu.meaningtree.nodes.declarations.VariableDeclaration;
import org.vstu.meaningtree.nodes.declarations.components.VariableDeclarator;
import org.vstu.meaningtree.nodes.enums.AugmentedAssignmentOperator;
import org.vstu.meaningtree.nodes.expressions.BinaryExpression;
import org.vstu.meaningtree.nodes.expressions.Identifier;
import org.vstu.meaningtree.nodes.expressions.ParenthesizedExpression;
import org.vstu.meaningtree.nodes.expressions.UnaryExpression;
import org.vstu.meaningtree.nodes.expressions.bitwise.*;
import org.vstu.meaningtree.nodes.expressions.calls.FunctionCall;
import org.vstu.meaningtree.nodes.expressions.comparison.*;
import org.vstu.meaningtree.nodes.expressions.identifiers.SimpleIdentifier;
import org.vstu.meaningtree.nodes.expressions.literals.FloatLiteral;
import org.vstu.meaningtree.nodes.expressions.literals.IntegerLiteral;
import org.vstu.meaningtree.nodes.expressions.literals.NumericLiteral;
import org.vstu.meaningtree.nodes.expressions.logical.NotOp;
import org.vstu.meaningtree.nodes.expressions.logical.ShortCircuitAndOp;
import org.vstu.meaningtree.nodes.expressions.logical.ShortCircuitOrOp;
import org.vstu.meaningtree.nodes.expressions.math.*;
import org.vstu.meaningtree.nodes.expressions.other.AssignmentExpression;
import org.vstu.meaningtree.nodes.expressions.other.IndexExpression;
import org.vstu.meaningtree.nodes.expressions.other.TernaryOperator;
import org.vstu.meaningtree.nodes.expressions.unary.*;
import org.vstu.meaningtree.nodes.statements.ExpressionSequence;
import org.vstu.meaningtree.nodes.statements.ExpressionStatement;
import org.vstu.meaningtree.nodes.types.UserType;
import org.vstu.meaningtree.nodes.types.builtin.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class CppLanguage extends LanguageParser {
    private final TSLanguage _language;
    private final TSParser _parser;
    private final Map<String, UserType> _userTypes;

    public CppLanguage() {
        _language = new TreeSitterCpp();
        _parser = new TSParser();
        _parser.setLanguage(_language);
        _userTypes = new HashMap<>();
    }

    @NotNull
    public MeaningTree getMeaningTree(String code) {
        _code = code;

        TSTree tree = _parser.parseString(null, code);
        try {
            tree.printDotGraphs(new File("TSTree.dot"));
        } catch (IOException e) { }

        return new MeaningTree(fromTSNode(tree.getRootNode()));
    }

    @NotNull
    private Node fromTSNode(@NotNull TSNode node) {
        Objects.requireNonNull(node);

        if (node.hasError()) {
            throw new IllegalArgumentException("Cannot parse code containing errors: " + node);
        }

        return switch (node.getType()) {
            case "translation_unit" -> fromTranslationUnit(node);
            case "expression_statement" -> fromExpressionStatement(node);
            case "binary_expression" -> fromBinaryExpression(node);
            case "unary_expression" -> fromUnaryExpression(node);
            case "parenthesized_expression" -> fromParenthesizedExpression(node);
            case "update_expression" -> fromUpdateExpression(node);
            case "call_expression" -> fromCallExpression(node);
            case "conditional_expression" -> fromConditionalExpression(node);
            case "comma_expression" -> fromCommaExpression(node);
            case "subscript_expression" -> fromSubscriptExpression(node);
            case "assignment_expression" -> fromAssignmentExpression(node);
            case "primitive_type" -> fromPrimitiveType(node);
            case "sized_type_specifier" -> fromSizedTypeSpecifier(node);
            case "declaration" -> fromDeclaration(node);
            case "identifier" -> fromIdentifier(node);
            case "number_literal" -> fromNumberLiteral(node);
            case "user_defined_literal" -> fromUserDefinedLiteral(node);
            default -> throw new UnsupportedOperationException(String.format("Can't parse %s this code:\n%s", node.getType(), getCodePiece(node)));
        };
    }

    @NotNull
    private NumericLiteral fromUserDefinedLiteral(@NotNull TSNode node) {
        String value = getCodePiece(node.getChildByFieldName("number_literal"));
        String literalSuffix = getCodePiece(node.getChildByFieldName("literal_suffix"));

        if (literalSuffix.equals("f") || literalSuffix.equals("F")) {
            return new FloatLiteral(value, false);
        }

        throw new IllegalArgumentException(
                "Can't parse user defined literal with \"%s\" value and \"%s\" literal suffix".formatted(
                        value,
                        literalSuffix
                )
        );
    }

    @NotNull
    private ExpressionSequence fromSubscriptArgumentList(@NotNull TSNode node) {
        var arguments = new ArrayList<Expression>();
        for (int i = 0; i < node.getNamedChildCount(); i++) {
            TSNode tsArgument = node.getNamedChild(i);
            Expression argument = (Expression) fromTSNode(tsArgument);
            arguments.add(argument);
        }
        return new ExpressionSequence(arguments);
    }

    @NotNull
    private IndexExpression fromSubscriptExpression(@NotNull TSNode node) {
        Expression argument = (Expression) fromTSNode(node.getChildByFieldName("argument"));
        ExpressionSequence indices = fromSubscriptArgumentList(node.getChildByFieldName("indices"));
        return new IndexExpression(argument, indices);
    }

    @NotNull
    private ExpressionSequence fromCommaExpression(@NotNull TSNode node) {
        var expressions = new ArrayList<Expression>();

        TSNode tsLeft = node.getChildByFieldName("left");
        expressions.add((Expression) fromTSNode(tsLeft));

        TSNode tsRight = node.getChildByFieldName("right");
        while (tsRight.getType().equals("comma_expression")) {
            tsLeft = tsRight.getChildByFieldName("left");
            expressions.add((Expression) fromTSNode(tsLeft));

            tsRight = tsRight.getChildByFieldName("right");
        }
        expressions.add((Expression) fromTSNode(tsRight));

        return new ExpressionSequence(expressions);
    }

    @NotNull
    private TernaryOperator fromConditionalExpression(@NotNull TSNode node) {
        Expression condition = (Expression) fromTSNode(node.getChildByFieldName("condition"));
        Expression consequence = (Expression) fromTSNode(node.getChildByFieldName("consequence"));
        Expression alternative = (Expression) fromTSNode(node.getChildByFieldName("alternative"));
        return new TernaryOperator(condition, consequence, alternative);
    }

    @NotNull
    private FunctionCall fromCallExpression(@NotNull TSNode node) {
        Identifier functionName = (Identifier) fromTSNode(node.getChildByFieldName("function"));

        TSNode tsArguments = node.getChildByFieldName("arguments");
        List<Expression> arguments = new ArrayList<>();
        for (int i = 0; i < tsArguments.getNamedChildCount(); i++) {
            TSNode tsArgument = tsArguments.getNamedChild(i);
            Expression argument = (Expression) fromTSNode(tsArgument);
            arguments.add(argument);
        }

        return new FunctionCall(functionName, arguments);
    }

    @NotNull
    private ParenthesizedExpression fromParenthesizedExpression(@NotNull TSNode node) {
        Expression expr = (Expression) fromTSNode(node.getChild(1));
        return new ParenthesizedExpression(expr);
    }

    @NotNull
    private UnaryExpression fromUnaryExpression(@NotNull TSNode node) {
        Expression argument = (Expression) fromTSNode(node.getChildByFieldName("operand"));
        TSNode operation = node.getChildByFieldName("operator");
        return switch (getCodePiece(operation)) {
            case "!" -> new NotOp(argument);
            case "~" -> new InversionOp(argument);
            case "-" -> new UnaryMinusOp(argument);
            case "+" -> new UnaryPlusOp(argument);
            default -> throw new UnsupportedOperationException();
        };
    }

    @NotNull
    private UnaryExpression fromUpdateExpression(@NotNull TSNode node) {
        String code = getCodePiece(node);

        if (code.endsWith("++")) {
            Identifier identifier = (Identifier) fromTSNode(node.getChild(0));
            return new PostfixIncrementOp(identifier);
        }
        else if (code.startsWith("++")) {
            Identifier identifier = (Identifier) fromTSNode(node.getChild(1));
            return new PrefixIncrementOp(identifier);
        }
        else if (code.endsWith("--")) {
            Identifier identifier = (Identifier) fromTSNode(node.getChild(0));
            return new PostfixDecrementOp(identifier);
        }
        else if (code.startsWith("--")) {
            Identifier identifier = (Identifier) fromTSNode(node.getChild(1));
            return new PrefixDecrementOp(identifier);
        }

        throw new IllegalArgumentException();
    }

    @NotNull
    private BinaryExpression fromBinaryExpression(@NotNull TSNode node) {
        Expression left = (Expression) fromTSNode(node.getChildByFieldName("left"));
        Expression right = (Expression) fromTSNode(node.getChildByFieldName("right"));
        TSNode operator = node.getChildByFieldName("operator");

        return switch (getCodePiece(operator)) {
            case "+" -> new AddOp(left, right);
            case "-" -> new SubOp(left, right);
            case "*" -> new MulOp(left, right);
            case "/" -> new DivOp(left, right);
            case "%" -> new ModOp(left, right);
            case "<" -> new LtOp(left, right);
            case ">" -> new GtOp(left, right);
            case "==" -> new EqOp(left, right);
            case "!=" -> new NotEqOp(left, right);
            case ">=" -> new GeOp(left, right);
            case "<=" -> new LeOp(left, right);
            case "&&" -> new ShortCircuitAndOp(left, right);
            case "||" -> new ShortCircuitOrOp(left, right);
            case "&" -> new BitwiseAndOp(left, right);
            case "|" -> new BitwiseOrOp(left, right);
            case "^" -> new XorOp(left, right);
            case "<<" -> new LeftShiftOp(left, right);
            case ">>" -> new RightShiftOp(left, right);
            default -> throw new UnsupportedOperationException(String.format("Can't parse operator %s", getCodePiece(operator)));
        };
    }

    @NotNull
    private Type fromSizedTypeSpecifier(@NotNull TSNode node) {
        String type = getCodePiece(node);

        if (type.matches(".*(long|int|short|unsigned).*")) {
            return getNumericType(type);
        } else {
            throw new UnsupportedOperationException(String.format("Can't parse sized type %s this code:\n%s", node.getType(), getCodePiece(node)));
        }
    }

    @NotNull
    private NumericType getNumericType(@NotNull String type) {
        boolean isUnsigned = false;
        int size = 32;

        if (type.contains("unsigned")) {
            isUnsigned = true;
        }

        if (type.contains("long")) {
            size *= (int) Math.pow(2, StringUtils.countMatches(type, "long"));
        } else if (type.contains("short")) {
            size = 16;
        }

        return new IntType(size, isUnsigned);
    }

    @NotNull
    private Type fromPrimitiveType(@NotNull TSNode node) {
        String typeName = getCodePiece(node);
        return switch (typeName) {
            case "int" -> new IntType();
            case "double" -> new FloatType(64);
            case "float" -> new FloatType();
            case "char" -> new CharacterType();
            case "w_char" -> new CharacterType(16);
            case "bool" -> new BooleanType();
            case "void" -> new VoidType();
            default -> throw new UnsupportedOperationException(String.format("Can't parse type in %s this code:\n%s", node.getType(), getCodePiece(node)));
        };
    }

    @NotNull
    private VariableDeclarator fromVariableDeclarator(@NotNull TSNode node) {
        TSNode tsVariableName = node.getChildByFieldName("declarator");
        TSNode tsValue = node.getChildByFieldName("value");

        SimpleIdentifier variableName = (SimpleIdentifier) fromTSNode(tsVariableName);
        Expression value = (Expression) fromTSNode(tsValue);

        return new VariableDeclarator(variableName, value);
    }

    @NotNull
    private VariableDeclaration fromDeclaration(@NotNull TSNode node) {
        Type type = (Type) fromTSNode(node.getChildByFieldName("type"));

        var declarators = new ArrayList<VariableDeclarator>();
        // Пропускаем узел type, поэтому i = 1
        for (int i = 1; i < node.getNamedChildCount(); i++) {
            TSNode tsDeclarator = node.getNamedChild(i);

            if (!tsDeclarator.getType().equals("init_declarator")) {
                throw new IllegalArgumentException(
                        "Unsupported type of node in declaration: " + tsDeclarator.getType()
                );
            }

            var declarator = fromVariableDeclarator(tsDeclarator);
            declarators.add(declarator);
        }

        return new VariableDeclaration(type, declarators);
    }

    @NotNull
    private Identifier fromIdentifier(@NotNull TSNode node) {
        return new SimpleIdentifier(getCodePiece(node));
    }

    @NotNull
    private AssignmentExpression fromAssignmentExpression(@NotNull TSNode node) {
        String variableName = getCodePiece(node.getChildByFieldName("left"));
        SimpleIdentifier identifier = new SimpleIdentifier(variableName);
        Expression right = (Expression) fromTSNode(node.getChildByFieldName("right"));

        String operatorType = node.getChildByFieldName("operator").getType();
        AugmentedAssignmentOperator augmentedAssignmentOperator = switch (operatorType) {
            case "=" -> AugmentedAssignmentOperator.NONE;
            case "+=" -> AugmentedAssignmentOperator.ADD;
            case "-=" -> AugmentedAssignmentOperator.SUB;
            case "*=" -> AugmentedAssignmentOperator.MUL;
            // Возможно тип AugmentedAssignmentOperator надо определять исходя из типа аргументов
            case "/=" -> AugmentedAssignmentOperator.DIV;
            case "&=" -> AugmentedAssignmentOperator.BITWISE_AND;
            case "|=" -> AugmentedAssignmentOperator.BITWISE_OR;
            case "^=" -> AugmentedAssignmentOperator.BITWISE_XOR;
            case "<<=" -> AugmentedAssignmentOperator.BITWISE_SHIFT_LEFT;
            case ">>=" -> AugmentedAssignmentOperator.BITWISE_SHIFT_RIGHT;
            case "%=" -> AugmentedAssignmentOperator.MOD;
            default -> throw new IllegalStateException("Unexpected augmented assignment type: " + operatorType);
        };

        return new AssignmentExpression(identifier, right, augmentedAssignmentOperator);
    }

    @NotNull
    private NumericLiteral fromNumberLiteral(@NotNull TSNode node) {
        String value = getCodePiece(node);
        if (value.contains(".")) {
            return new FloatLiteral(value);
        }
        return new IntegerLiteral(value, false, false);
    }

    @NotNull
    private Node fromTranslationUnit(@NotNull TSNode node) {
        return fromTSNode(node.getChild(0));
    }

    @NotNull
    private ExpressionStatement fromExpressionStatement(@NotNull TSNode node) {
        Expression expr = (Expression) fromTSNode(node.getChild(0));
        return new ExpressionStatement(expr);
    }
}
