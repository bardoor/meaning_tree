package org.vstu.meaningtree.languages.parsers;
import org.treesitter.*;
import org.vstu.meaningtree.MeaningTree;
import org.vstu.meaningtree.nodes.ParenthesizedExpression;
import org.vstu.meaningtree.nodes.Type;
import org.vstu.meaningtree.nodes.*;
import org.vstu.meaningtree.nodes.bitwise.BitwiseAndOp;
import org.vstu.meaningtree.nodes.bitwise.BitwiseOrOp;
import org.vstu.meaningtree.nodes.declarations.*;
import org.vstu.meaningtree.nodes.definitions.ClassDefinition;
import org.vstu.meaningtree.nodes.definitions.MethodDefinition;
import org.vstu.meaningtree.nodes.identifiers.ScopedIdentifier;
import org.vstu.meaningtree.nodes.identifiers.SimpleIdentifier;
import org.vstu.meaningtree.nodes.literals.StringLiteral;
import org.vstu.meaningtree.nodes.logical.ShortCircuitAndOp;
import org.vstu.meaningtree.nodes.logical.ShortCircuitOrOp;
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
import org.vstu.meaningtree.nodes.types.*;
import org.vstu.meaningtree.nodes.unary.PostfixDecrementOp;
import org.vstu.meaningtree.nodes.unary.PostfixIncrementOp;
import org.vstu.meaningtree.nodes.unary.PrefixDecrementOp;
import org.vstu.meaningtree.nodes.unary.PrefixIncrementOp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JavaLanguage extends Language {
    TSLanguage _language;
    TSParser _parser;

    public JavaLanguage() {
        _language = new TreeSitterJava();
        _parser = new TSParser();
        _parser.setLanguage(_language);
    }

    public MeaningTree getMeaningTree(String code) {
        _code = code;

        TSTree tree = _parser.parseString(null, code);
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
            case "while_statement" -> fromWhileTSNode(node);
            case "update_expression" -> fromUpdateExpressionTSNode(node);
            case "package_declaration" -> fromPackageDeclarationTSNode(node);
            case "scoped_identifier" -> fromScopedIdentifierTSNode(node);
            case "class_declaration" -> fromClassDeclarationTSNode(node);
            case "field_declaration" -> fromFieldDeclarationTSNode(node);
            case "string_literal" -> fromStringLiteralTSNode(node);
            case "method_declaration" -> fromMethodDeclarationTSNode(node);
            case "switch_expression" -> fromSwitchExpressionTSNode(node);
            case "break_statement" -> fromBreakStatementTSNode(node);
            case "continue_statement" -> fromContinueStatementTSNode(node);
            case null, default -> throw new UnsupportedOperationException(String.format("Can't parse %s", node.getType()));
        };
    }

    private Node fromContinueStatementTSNode(TSNode continueNode) {
        return new ContinueStatement();
    }

    private Node fromBreakStatementTSNode(TSNode breakNode) {
        return new BreakStatement();
    }

    private ConditionBranch fromSwitchGroupTSNode(TSNode switchGroup) {
        Expression condition =
                (Expression) fromTSNode(switchGroup.getNamedChild(0).getNamedChild(0));

        List<Node> statements = new ArrayList<>();
        for (int i = 1; i < switchGroup.getNamedChildCount(); i++) {
            statements.add(fromTSNode(switchGroup.getNamedChild(i)));
        }

        CompoundStatement body = new CompoundStatement(statements);
        return new ConditionBranch(condition, body);
    }

    private Node fromSwitchExpressionTSNode(TSNode switchNode) {
        Expression condition =
                (Expression) fromTSNode(switchNode.getChildByFieldName("condition").getNamedChild(0));

        Statement defaultCase = null;
        List<ConditionBranch> cases = new ArrayList<>();

        TSNode switchBlock = switchNode.getChildByFieldName("body");
        for (int i = 0; i < switchBlock.getNamedChildCount(); i++) {
            TSNode switchGroup = switchBlock.getNamedChild(i);

            String labelName = getCodePiece(switchGroup.getNamedChild(0));
            if (labelName.equals("default")) {
                List<Node> statements = new ArrayList<>();

                for (int j = 1; j < switchGroup.getNamedChildCount(); j++) {
                    statements.add(fromTSNode(switchGroup.getNamedChild(j)));
                }

                defaultCase = new CompoundStatement(statements);
            }
            else {
                ConditionBranch caseBlock = fromSwitchGroupTSNode(switchGroup);
                cases.add(caseBlock);
            }
        }

        return new SwitchStatement(condition, cases, defaultCase);
    }

    private MethodDefinition fromMethodDeclarationTSNode(TSNode node) {
        List<Modifier> modifiers = new ArrayList<>();
        if (node.getChild(0).getType().equals("modifiers")) {
            modifiers.addAll(fromModifiers(node.getChild(0)));
        }

        Type returnType = fromTypeTSNode(node.getChildByFieldName("type"));
        Identifier identifier = fromScopedIdentifierTSNode(node.getChildByFieldName("name"));
        List<DeclarationArgument> parameters = fromMethodParameters(node.getChildByFieldName("parameters"));

        // Пока не реализован механизм нахождения класса, к которому принадлежит метод,
        // и определение аннотаций
        MethodDeclaration declaration = new MethodDeclaration(null,
                identifier,
                returnType,
                List.of(),
                modifiers,
                parameters
        );

        CompoundStatement body = fromBlockTSNode(node.getChildByFieldName("body"));

        return new MethodDefinition(declaration, body);
    }

    private List<DeclarationArgument> fromMethodParameters(TSNode node) {
        List<DeclarationArgument> parameters = new ArrayList<>();

        for (int i = 0; i < node.getChildCount(); i++) {
            // TODO: может быть можно как-то более эффективно извлекать извлечь параметры...
            // Такие сложности из-за того, что в детях также будет скобки, запятые и другие
            // синтаксические артефакты.
            TSNode child = node.getChild(i);
            if (!child.getType().equals("formal_parameter")) {
                continue;
            }

            DeclarationArgument parameter = fromFormalParameter(child);
            parameters.add(parameter);
        }

        return parameters;
    }

    private DeclarationArgument fromFormalParameter(TSNode node) {
        Type type = fromTypeTSNode(node.getChildByFieldName("type"));
        SimpleIdentifier name = (SimpleIdentifier) fromIdentifierTSNode(node.getChildByFieldName("name"));
        // Не поддерживается распаковка списков (как в Python) и значения по умолчанию
        return new DeclarationArgument(type, false, name, null);
    }

    private StringLiteral fromStringLiteralTSNode(TSNode node) {
        StringBuilder builder = new StringBuilder();

        // Первый и последний ребенок - кавычки, их пропускаем.
        // Дети string_literal это либо string_fragment, либо escape_sequence,
        // поэтому ничего экранировать не нужно, они уже представлены так как надо
        for (int i = 1; i < node.getChildCount() - 1; i++) {
            builder.append(getCodePiece(node.getChild(i)));
        }

        return StringLiteral.fromEscaped(builder.toString(), StringLiteral.Type.NONE);
    }

    private FieldDeclaration fromFieldDeclarationTSNode(TSNode node) {
        int currentChildIndex = 0;

        List<Modifier> modifiers = new ArrayList<>();
        if (node.getChild(currentChildIndex).getType().equals("modifiers")) {
            modifiers.addAll(fromModifiers(node.getChild(currentChildIndex)));
        }

        VariableDeclaration declaration = fromVariableDeclarationTSNode(node);
        return new FieldDeclaration(declaration.getType(), modifiers, declaration.getDeclarators());
    }

    private List<Modifier> fromModifiers(TSNode node) {
        // Внутри происходит считывание лишь модификаторов области видимости,
        // причем допускается всего лишь 1 или 0 идентификаторов (несмотря на список).
        // Должно ли так быть - неизвестно, нужно разобраться...
        List<Modifier> modifiers = new ArrayList<>();

        for (int i = 0; i < node.getChildCount(); i++) {
            modifiers.add(
                    switch (node.getChild(i).getType()) {
                        case "public" -> Modifier.PUBLIC;
                        case "private" -> Modifier.PRIVATE;
                        case "protected" -> Modifier.PROTECTED;
                        case "abstract" -> Modifier.ABSTRACT;
                        case "final" -> Modifier.CONST;
                        case "static" -> Modifier.STATIC;
                        default -> throw new IllegalArgumentException("Unknown identifier: %s".formatted(node.getChild(i).getType()));
                    }
            );
        }

        return modifiers;
    }

    private ClassDefinition fromClassDeclarationTSNode(TSNode node) {
        int currentChildIndex = 0;

        List<Modifier> modifiers = new ArrayList<>();
        if (node.getChild(currentChildIndex).getType().equals("modifiers")) {
            modifiers.addAll(fromModifiers(node.getChild(currentChildIndex)));
            currentChildIndex++;
        }

        // Скипаем слово "class"
        currentChildIndex++;

        Identifier className = fromIdentifierTSNode(node.getChild(currentChildIndex));
        currentChildIndex++;

        // Парсим тело класса как блочное выражение... Правильно ли? Кто знает...
        CompoundStatement classBody = fromBlockTSNode(node.getChild(currentChildIndex));
        currentChildIndex++;

        ClassDeclaration decl = new ClassDeclaration(modifiers, className);
        // TODO: нужно поменять getNodes() у CompoundStatement, чтобы он не массив возвращал
        return new ClassDefinition(decl, classBody);
    }

    private ScopedIdentifier fromScopedIdentifierTSNode(TSNode node) {
        TSNode scope = node;

        List<SimpleIdentifier> scopes = new ArrayList<>();
        while (scope.getType().equals("scoped_identifier")) {
            scopes.add((SimpleIdentifier) fromIdentifierTSNode(scope.getChildByFieldName("name")));
            scope = scope.getChildByFieldName("scope");
        }
        scopes.add((SimpleIdentifier) fromIdentifierTSNode(scope));

        return new ScopedIdentifier(scopes.reversed());
    }

    private PackageDeclaration fromPackageDeclarationTSNode(TSNode node) {
        TSNode packageName = node.getChild(1);
        return new PackageDeclaration(fromIdentifierTSNode(packageName));
    }

    private Node fromUpdateExpressionTSNode(TSNode node) {
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

    private Identifier fromIdentifierTSNode(TSNode node) {
        if (node.getType().equals("scoped_identifier")) {
            return fromScopedIdentifierTSNode(node);
        }
        String variableName = getCodePiece(node);
        return new SimpleIdentifier(variableName);
    }

    private AssignmentExpression fromAssignmentExpressionTSNode(TSNode node) {
        String variableName = getCodePiece(node.getChildByFieldName("left"));
        SimpleIdentifier identifier = new SimpleIdentifier(variableName);
        Expression right = (Expression) fromTSNode(node.getChildByFieldName("right"));
        return new AssignmentExpression(identifier, right);
    }

    private GeneralForLoop fromForStatementTSNode(TSNode node) {
        HasInitialization init = null;
        Expression condition = null;
        Expression update = null;

        if (!node.getChildByFieldName("init").isNull()) {
            init = (HasInitialization) fromTSNode(node.getChildByFieldName("init"));
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

    private VariableDeclarator fromVariableDeclarator(TSNode node) {
        String name = getCodePiece(node.getChildByFieldName("name"));
        SimpleIdentifier ident = new SimpleIdentifier(name);

        if (!node.getChildByFieldName("value").isNull()) {
            Expression value = (Expression) fromTSNode(node.getChildByFieldName("value"));
            return new VariableDeclarator(ident, value);
        }
        else {
            return new VariableDeclarator(ident);
        }
    }

    private int countArrayDimensions(TSNode dimensionsNode) {
        String dimensions = getCodePiece(dimensionsNode);
        return dimensions.split("\\u005b\\u005d", -1).length - 1;
    }

    private Type fromTypeTSNode(TSNode node) {
        String type = node.getType();
        String typeName = getCodePiece(node);
        Type parsedType;

        switch (type) {
            case "integral_type":
                parsedType = switch(typeName) {
                    case "int", "short", "long", "byte", "char" -> new IntType();
                    default -> throw new IllegalStateException("Unexpected value: " + typeName);
                };
                break;
            case "floating_point_type":
                parsedType = new FloatType();
                break;
            case "boolean_type":
                parsedType = new BooleanType();
                break;
            case "array_type":
                Type baseType = fromTypeTSNode(node.getChildByFieldName("element"));
                TSNode dimensions = node.getChildByFieldName("dimensions");
                int dimensionsCount = countArrayDimensions(dimensions);
                parsedType = new ArrayType(baseType, dimensionsCount);
                break;
            case "void_type":
                parsedType = new VoidType();
                break;
            case "type_identifier":
                if (typeName.equals("String")) {
                    parsedType = new StringType();
                }
                else if (typeName.equals("Object")) {
                    parsedType = new UnknownType();
                }
                else {
                    throw new IllegalStateException("Unexpected type: " + typeName);
                }
                break;
            default:
                throw new IllegalStateException("Unexpected type: " + typeName);
        }

        return parsedType;
    }

    private VariableDeclaration fromVariableDeclarationTSNode(TSNode node) {
        List<Modifier> modifiers = new ArrayList<>();
        TSNode possibleModifiers = node.getChild(0);
        if (possibleModifiers.getType().equals("modifiers")) {
            modifiers.addAll(fromModifiers(possibleModifiers));
        }

        Type type = fromTypeTSNode(node.getChildByFieldName("type"));

        List<VariableDeclarator> declarators = new ArrayList<>();

        TSQuery all_declarators = new TSQuery(_language, "(variable_declarator) @decls");
        TSQueryCursor cursor = new TSQueryCursor();
        cursor.exec(all_declarators, node);
        TSQueryMatch match = new TSQueryMatch();
        while (cursor.nextMatch(match)) {
            TSQueryCapture capture = match.getCaptures()[0];
            VariableDeclarator decl = fromVariableDeclarator(capture.getNode());
            declarators.add(decl);
        }
        //while (cursor.nextMatch(match)) {
        //    System.out.println(match.getCaptures());
        //}

        if (!modifiers.isEmpty()) {
            return new FieldDeclaration(type, modifiers, declarators);
        }

        return new VariableDeclaration(type, declarators);
    }

    private ProgramEntryPoint _fromProgramTSNode(TSNode node) {
        if (node.getChildCount() == 0) {
            throw new IllegalArgumentException();
        }

        PackageDeclaration packageDeclaration = fromPackageDeclarationTSNode(node.getChild(0));

        ClassDefinition classDefinition = fromClassDeclarationTSNode(node.getChild(1));

        List<Node> body = List.of(packageDeclaration, classDefinition);

        Optional<MethodDefinition> mainMethod = classDefinition.findMethod("main");
        return mainMethod.map(methodDefinition -> new ProgramEntryPoint(body, classDefinition, methodDefinition)).orElseGet(() -> new ProgramEntryPoint(body, classDefinition));
    }

    private ProgramEntryPoint fromProgramTSNode(TSNode node) {
        List<Node> nodes = new ArrayList<>();
        for (int i = 0; i < node.getNamedChildCount(); i++) {
            nodes.add(fromTSNode(node.getNamedChild(i)));
        }

        ClassDefinition mainClass = null;
        MethodDefinition mainMethod = null;
        for (Node n : nodes) {
            // Только один класс в файле может иметь модификатор public,
            // поэтому он и является главным классом
            if (n instanceof ClassDefinition classDefinition
                    && classDefinition.getModifiers().contains(Modifier.PUBLIC)) {
                mainClass = classDefinition;

                Optional<MethodDefinition> m = mainClass.findMethod("main");
                if (m.isPresent()) {
                    mainMethod = m.get();
                }
            }
        }

        return new ProgramEntryPoint(nodes, mainClass, mainMethod);
    }

    private WhileLoop fromWhileTSNode(TSNode node) {
        TSNode tsCond = node.getChildByFieldName("condition");
        Expression mtCond = (Expression) fromTSNode(tsCond);

        TSNode tsBody = node.getChildByFieldName("body");
        Statement mtBody = (Statement) fromTSNode(tsBody);

        return new WhileLoop(mtCond, mtBody);
    }

    private CompoundStatement fromBlockTSNode(TSNode node) {
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

    private IfStatement fromIfStatementTSNode(TSNode node) {
        // Берем ребенка под индексом 1, чтобы избежать захвата скобок, а значит
        // неправильного парсинга (получаем выражение в скобках в качестве условия, а не просто выражение)
        Expression condition = (Expression) fromTSNode(node.getChildByFieldName("condition").getChild(1));
        Statement consequence = (Statement) fromTSNode(node.getChildByFieldName("consequence"));
        Statement alternative = (Statement) fromTSNode(node.getChildByFieldName("alternative"));
        return new IfStatement(condition, consequence, alternative);
    }

    private Node fromConditionTSNode(TSNode node) {
        // TODO: Что-то сделать с этим...
        // У condition дети: '(', 'binary_expression', ')'
        // По имени binary_expression почему-то получить не удалось
        return fromTSNode(node.getChild(1));
    }

    private ExpressionStatement fromExpressionStatementTSNode(TSNode node) {
        Expression expr = (Expression) fromTSNode(node.getChild(0));
        return new ExpressionStatement(expr);
    }

    private ParenthesizedExpression fromParenthesizedExpressionTSNode(TSNode node) {
        Expression expr = (Expression) fromTSNode(node.getChild(1));
        return new ParenthesizedExpression(expr);
    }

    private IntegerLiteral fromIntegerLiteralTSNode(TSNode node) {
        String value = getCodePiece(node);
        return new IntegerLiteral(value);
    }

    private FloatLiteral fromFloatLiteralTSNode(TSNode node) {
        String value = getCodePiece(node);
        return new FloatLiteral(value);
    }

    private UnaryExpression fromUnaryExpressionTSNode(TSNode node) {
        Expression argument = (Expression) fromTSNode(node.getChildByFieldName("operand"));
        TSNode operation = node.getChildByFieldName("operator");
        return switch (getCodePiece(operation)) {
            case "!" -> new NotOp(argument);
            case null, default -> throw new UnsupportedOperationException();
        };
    }

    private BinaryExpression fromBinaryExpressionTSNode(TSNode node) {
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
            case "&&" -> new ShortCircuitAndOp(left, right);
            case "||" -> new ShortCircuitOrOp(left, right);
            case "&" -> new BitwiseAndOp(left, right);
            case "|" -> new BitwiseOrOp(left, right);
            default -> throw new UnsupportedOperationException(String.format("Can't parse operator %s", getCodePiece(operator)));
        };
    }


}
