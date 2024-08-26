package org.vstu.meaningtree.languages.parsers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.treesitter.*;
import org.vstu.meaningtree.MeaningTree;
import org.vstu.meaningtree.nodes.Node;
import org.vstu.meaningtree.nodes.literals.FloatLiteral;
import org.vstu.meaningtree.nodes.literals.IntegerLiteral;
import org.vstu.meaningtree.nodes.literals.NumericLiteral;
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

    @Nullable
    private Node fromTSNode(@NotNull TSNode node) {
        Objects.requireNonNull(node);

        if (node.hasError()) {
            throw new IllegalArgumentException("Cannot parse code containing errors: " + node);
        }

        return switch(node.getType()) {
            case "translation_unit" -> fromTranslationUnit(node);
            case "expression_statement" -> fromExprStatement(node);
            case "binary_expression" -> fromBinaryExpression(node);
            case "primitive_type" -> fromPrimitiveType(node);
            case "init_declarator" -> fromInitDeclarators(node);
            case "declaration" -> fromDeclaration(node);
            case "identifier" -> fromIdentifier(node);
            case "number_literal" -> fromNumberLiteral(node);

            default -> throw new UnsupportedOperationException(String.format("Can't parse %s this code:\n%s", node.getType(), getCodePiece(node)));
        };
    }

    private Node fromTranslationUnit(TSNode node) {
    }

    private NumericLiteral fromNumberLiteral(TSNode node) {
        String value = getCodePiece(node);
        if (value.startsWith("0") || !value.contains(".")) {
            return new IntegerLiteral(value);
        }
        else {
            return new FloatLiteral(value);
        }
    }

    private Node fromIdentifier(TSNode node) {
    }

    private Node fromDeclaration(TSNode node) {
    }

    private Node fromInitDeclarators(TSNode node) {
    }

    private Node fromPrimitiveType(TSNode node) {
    }

    @NotNull
    private Node fromBinaryExpression(TSNode node) {
    }

    @NotNull
    private ExpressionStatement fromExprStatement(@NotNull TSNode node) {
        return null;
    }



}
