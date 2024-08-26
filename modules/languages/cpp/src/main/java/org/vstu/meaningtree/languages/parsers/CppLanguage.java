package org.vstu.meaningtree.languages.parsers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.treesitter.*;
import org.vstu.meaningtree.MeaningTree;
import org.vstu.meaningtree.nodes.AssignmentExpression;
import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Node;
import org.vstu.meaningtree.nodes.identifiers.Identifier;
import org.vstu.meaningtree.nodes.identifiers.ScopedIdentifier;
import org.vstu.meaningtree.nodes.identifiers.SimpleIdentifier;
import org.vstu.meaningtree.nodes.literals.FloatLiteral;
import org.vstu.meaningtree.nodes.literals.IntegerLiteral;
import org.vstu.meaningtree.nodes.literals.Literal;
import org.vstu.meaningtree.nodes.literals.NumericLiteral;
import org.vstu.meaningtree.nodes.statements.AssignmentStatement;
import org.vstu.meaningtree.nodes.statements.ExpressionStatement;
import org.vstu.meaningtree.nodes.types.UserType;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
public class CppLanguage extends Language {
    private final TSLanguage _language;
    private final TSParser _parser;
    private final Map<String, UserType> _userTypes;

    public CppLanguage() {
        _language = new TreeSitterCpp();
        _parser = new TSParser();
        _parser.setLanguage(_language);
        _userTypes = new HashMap<>();
    }

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

        return switch(node.getType()) {
            case "translation_unit" -> fromTranslationUnit(node);
            case "expression_statement" -> fromExprStatement(node);
            //case "binary_expression" -> fromBinaryExpression(node);
            case "assignment_expression" -> fromAssignmentExpression(node);
            //case "primitive_type" -> fromPrimitiveType(node);
            //case "init_declarator" -> fromInitDeclarators(node);
            //case "declaration" -> fromDeclaration(node);
            case "identifier" -> fromIdentifier(node);
            case "number_literal" -> fromNumberLiteral(node);

            default -> throw new UnsupportedOperationException(String.format("Can't parse %s this code:\n%s", node.getType(), getCodePiece(node)));
        };
    }

    private Node fromIdentifier(@NotNull TSNode node) {
        return new SimpleIdentifier(getCodePiece(node));
    }

    @NotNull
    private Node fromAssignmentExpression(@NotNull TSNode node) {
        String varName = getCodePiece(node.getChildByFieldName("left"));
        SimpleIdentifier identifier = new SimpleIdentifier(varName);
        Expression right = (Expression) fromTSNode(node.getChildByFieldName("right"));
        return new AssignmentExpression(identifier, right);
    }

    @NotNull
    private NumericLiteral fromNumberLiteral(@NotNull TSNode node) {
        String value = getCodePiece(node);
        if (value.startsWith("0") || !value.contains(".")) {
            return new IntegerLiteral(value);
        }
        else {
            return new FloatLiteral(value);
        }
    }

    private Node fromTranslationUnit(@NotNull TSNode node) {
        return fromTSNode(node.getChild(0));
    }

    @NotNull
    private ExpressionStatement fromExprStatement(@NotNull TSNode node) {
        Expression expr = (Expression) fromTSNode(node.getChild(0));
        return new ExpressionStatement(expr);
    }
}
