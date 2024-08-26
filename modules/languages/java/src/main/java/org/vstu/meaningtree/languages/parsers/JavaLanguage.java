package org.vstu.meaningtree.languages.parsers;
import org.apache.commons.text.StringEscapeUtils;
import org.jetbrains.annotations.Nullable;
import org.treesitter.*;
import org.vstu.meaningtree.MeaningTree;
import org.vstu.meaningtree.nodes.ParenthesizedExpression;
import org.vstu.meaningtree.nodes.Type;
import org.vstu.meaningtree.nodes.*;
import org.vstu.meaningtree.nodes.bitwise.*;
import org.vstu.meaningtree.nodes.declarations.*;
import org.vstu.meaningtree.nodes.definitions.ClassDefinition;
import org.vstu.meaningtree.nodes.definitions.MethodDefinition;
import org.vstu.meaningtree.nodes.definitions.ObjectConstructorDefinition;
import org.vstu.meaningtree.nodes.identifiers.Identifier;
import org.vstu.meaningtree.nodes.identifiers.ScopedIdentifier;
import org.vstu.meaningtree.nodes.identifiers.SelfReference;
import org.vstu.meaningtree.nodes.identifiers.SimpleIdentifier;
import org.vstu.meaningtree.nodes.literals.*;
import org.vstu.meaningtree.nodes.logical.ShortCircuitAndOp;
import org.vstu.meaningtree.nodes.logical.ShortCircuitOrOp;
import org.vstu.meaningtree.nodes.math.*;
import org.vstu.meaningtree.nodes.modules.*;
import org.vstu.meaningtree.nodes.statements.CompoundStatement;
import org.vstu.meaningtree.nodes.statements.*;
import org.vstu.meaningtree.nodes.comparison.*;
import org.vstu.meaningtree.nodes.logical.NotOp;
import org.vstu.meaningtree.nodes.types.*;
import org.vstu.meaningtree.nodes.types.Class;
import org.vstu.meaningtree.nodes.unary.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class JavaLanguage extends Language {
    private final TSLanguage _language;
    private final TSParser _parser;
    private final Map<String, UserType> _userTypes;

    public JavaLanguage() {
        _language = new TreeSitterJava();
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

    private Node fromTSNode(TSNode node) {
        Objects.requireNonNull(node);

        if (node.hasError()) {
            throw new IllegalArgumentException("Cannot parse code containing errors");
        }

        String nodeType = node.getType();
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
            case "decimal_integer_literal", "binary_integer_literal", "hex_integer_literal" -> fromIntegerLiteralTSNode(node);
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
            case "null_literal" -> fromNullLiteralTSNode(node);
            case "import_declaration" -> fromImportDeclarationTSNode(node);
            case "method_invocation" -> fromMethodInvocation(node);
            case "object_creation_expression" -> fromObjectCreationExpressionTSNode(node);
            case "true", "false" -> fromBooleanValueTSNode(node);
            case "field_access" -> fromFieldAccessTSNode(node);
            case "array_creation_expression" -> fromArrayCreationExpressionTSNode(node);
            case "array_initializer" -> fromArrayInitializer(node);
            case "return_statement" -> fromReturnStatementTSNode(node);
            case "line_comment", "block_comment" -> fromCommentTSNode(node);
            case "cast_expression" -> fromCastExpressionTSNode(node);
            case "array_access" -> fromArrayAccessTSNode(node);
            case "ternary_expression" -> fromTernaryExpressionTSNode(node);
            case "constructor_declaration" -> fromConstructorDeclarationTSNode(node);
            case "this" -> fromThisTSNode(node);
            case "character_literal" -> fromCharacterLiteralTSNode(node);
            case "do_statement" -> fromDoStatementTSNode(node);
            default -> throw new UnsupportedOperationException(String.format("Can't parse %s this code:\n%s", node.getType(), getCodePiece(node)));
        };
    }

    private Node fromDoStatementTSNode(TSNode node) {
        Statement body = (Statement) fromTSNode(node.getChildByFieldName("body"));
        Expression condition = (Expression) fromTSNode(node.getChildByFieldName("condition"));
        return new DoWhileLoop(condition, body);
    }

    private CharacterLiteral fromCharacterLiteralTSNode(TSNode node) {
        String representation = getCodePiece(node);
        String withoutQuotes = representation.substring(1, representation.length() - 1);
        int value = StringEscapeUtils.unescapeJava(withoutQuotes).codePointAt(0);
        return new CharacterLiteral(value);
    }

    private SelfReference fromThisTSNode(TSNode node) {
        return new SelfReference("this");
    }

    private Node fromConstructorDeclarationTSNode(TSNode node) {
        List<Modifier> modifiers;
        if (node.getNamedChild(0).getType().equals("modifiers"))
            { modifiers = fromModifiers(node.getNamedChild(0)); }
        else
            { modifiers = List.of(); }
        Identifier name = fromIdentifierTSNode(node.getChildByFieldName("name"));
        List<DeclarationArgument> parameters = fromMethodParameters(node.getChildByFieldName("parameters"));
        CompoundStatement body = fromBlockTSNode(node.getChildByFieldName("body"));
        // TODO: определение класса, к которому принадлежит метод и считывание аннотаций
        return new ObjectConstructorDefinition(null, name, List.of(), modifiers, parameters, body);
    }

    private Node fromTernaryExpressionTSNode(TSNode node) {
        Expression condition = (Expression) fromTSNode(node.getChildByFieldName("condition"));
        Expression consequence = (Expression) fromTSNode(node.getChildByFieldName("consequence"));
        Expression alternative = (Expression) fromTSNode(node.getChildByFieldName("alternative"));
        return new TernaryOperator(condition, consequence, alternative);
    }

    private Node fromArrayAccessTSNode(TSNode node) {
        Identifier arrayName = fromIdentifierTSNode(node.getChildByFieldName("array"));
        Expression index = (Expression) fromTSNode(node.getChildByFieldName("index"));
        return new IndexExpression(arrayName, index);
    }

    private Node fromCastExpressionTSNode(TSNode node) {
        Type castType = fromTypeTSNode(node.getChildByFieldName("type"));
        Expression value = (Expression) fromTSNode(node.getChildByFieldName("value"));
        return new CastTypeExpression(castType, value);
    }

    private Comment fromCommentTSNode(TSNode node) {
        String comment = getCodePiece(node);
        return switch (node.getType()) {
            case "line_comment" -> Comment.fromUnescaped(comment.substring(2));
            case "block_comment" -> Comment.fromUnescaped(comment.substring(2, comment.length() - 2));
            default -> throw new IllegalArgumentException();
        };
    }

    private Node fromReturnStatementTSNode(TSNode node) {
        if (node.getNamedChildCount() == 0) {
            return new ReturnStatement();
        }

        Expression expression = (Expression) fromTSNode(node.getNamedChild(0));
        return new ReturnStatement(expression);
    }

    private int countArrayDimensions(TSNode dimensionsNode) {
        String dimensions = getCodePiece(dimensionsNode);
        return dimensions.split("\\u005b\\u005d", -1).length - 1;
    }

    private Shape getArrayShape(TSNode arrayCreationNode) {
        int dimensionsCount = 0;
        Map<Integer, Expression> dimensions = new HashMap<>();

        // Начинаем с первого ребенка, т.к. нужно пропустить type-ребенка
        LOOP: for (int i = 1; i < arrayCreationNode.getNamedChildCount(); i++) {
            TSNode dimension = arrayCreationNode.getNamedChild(i);

            switch (dimension.getType()) {
                case "dimensions_expr" -> {
                    Expression dimensionExpr = (Expression) fromTSNode(dimension.getNamedChild(0));
                    dimensions.put(dimensionsCount, dimensionExpr);
                    dimensionsCount += 1;
                }
                case "dimensions" -> {
                    dimensionsCount += countArrayDimensions(dimension);
                }
                case "array_initializer" -> {
                    break LOOP;
                }
                default -> throw new IllegalStateException("Unexpected value: " + dimension.getType());
            }
        }

        List<Expression> realDimensions = new ArrayList<>(dimensionsCount);
        for (int i = 0; i < dimensionsCount; i++) {
            realDimensions.add(i, dimensions.getOrDefault(i, null));
        }

        return new Shape(dimensionsCount, realDimensions);
    }

    private ArrayInitializer fromArrayInitializer(TSNode arrayInitializerNode) {
        List<Expression> values = new ArrayList<>();
        for (int i = 0; i < arrayInitializerNode.getNamedChildCount(); i++) {
            Expression value = (Expression) fromTSNode(arrayInitializerNode.getNamedChild(i));
            values.add(value);
        }
        return new ArrayInitializer(values);
    }

    private ArrayNewExpression fromArrayCreationExpressionTSNode(TSNode arrayCreationNode) {
        Type arrayType = fromTypeTSNode(arrayCreationNode.getChildByFieldName("type"));
        Shape arrayShape = getArrayShape(arrayCreationNode);
        ArrayInitializer initializer = null;
        if (!arrayCreationNode.getChildByFieldName("value").isNull()) {
            initializer = fromArrayInitializer(arrayCreationNode.getChildByFieldName("value"));
        }
        return new ArrayNewExpression(arrayType, arrayShape, initializer);
    }

    private MemberAccess fromFieldAccessTSNode(TSNode fieldAccess) {
        Expression object = (Expression) fromTSNode(fieldAccess.getChildByFieldName("object"));
        SimpleIdentifier member = (SimpleIdentifier) fromIdentifierTSNode(fieldAccess.getChildByFieldName("field"));
        return new MemberAccess(object, member);
    }

    private BoolLiteral fromBooleanValueTSNode(TSNode node) {
        String value = getCodePiece(node);
        return switch (value) {
            case "true" -> new BoolLiteral(true);
            case "false" -> new BoolLiteral(false);
            default -> throw new IllegalStateException("Unexpected value " + value);
        };
    }

    private Node fromObjectCreationExpressionTSNode(TSNode objectCreationNode) {
        Type type = fromTypeTSNode(objectCreationNode.getChildByFieldName("type"));

        List<Expression> arguments = new ArrayList<>();
        TSNode tsArguments = objectCreationNode.getChildByFieldName("arguments");
        for (int i = 0; i < tsArguments.getNamedChildCount(); i++) {
            TSNode tsArgument = tsArguments.getNamedChild(i);
            Expression argument = (Expression) fromTSNode(tsArgument);
            arguments.add(argument);
        }

        return new ObjectNewExpression(type, arguments);
    }

    private FunctionCall fromMethodInvocation(TSNode methodInvocation) {
        Identifier methodName = fromIdentifierTSNode(methodInvocation.getChildByFieldName("name"));

        List<Expression> arguments = new ArrayList<>();
        TSNode tsArguments = methodInvocation.getChildByFieldName("arguments");
        for (int i = 0; i < tsArguments.getNamedChildCount(); i++) {
            TSNode tsArgument = tsArguments.getNamedChild(i);
            Expression argument = (Expression) fromTSNode(tsArgument);
            arguments.add(argument);
        }

        if (methodInvocation.getChildByFieldName("object").isNull()) {
            return new FunctionCall(methodName, arguments);
        }

        Expression object = (Expression) fromTSNode(methodInvocation.getChildByFieldName("object"));
        return new MethodCall(object, methodName, arguments);
    }

    private boolean isStaticImport(TSNode importDeclaration) {
        for (int i = 0; i < importDeclaration.getChildCount(); i++) {
            if (importDeclaration.getChild(i).getType().equals("static")) {
                return true;
            }
        }
        return false;
    }

    private boolean isWildcardImport(TSNode importDeclaration) {
        for (int i = 0; i < importDeclaration.getChildCount(); i++) {
            if (importDeclaration.getChild(i).getType().equals("asterisk")) {
                return true;
            }
        }
        return false;
    }

    private Node fromImportDeclarationTSNode(TSNode importDeclaration) {
        TSNode scopeNode = importDeclaration.getNamedChild(0);

        if (isStaticImport(importDeclaration)) {
            if (isWildcardImport(importDeclaration)) {
                Identifier scope = fromIdentifierTSNode(scopeNode);
                return new StaticImportAll(scope);
            }
            else {
                Identifier scope = fromIdentifierTSNode(scopeNode.getChildByFieldName("scope"));
                Identifier member = fromIdentifierTSNode(scopeNode.getChildByFieldName("name"));
                return new StaticImportMembers(scope, member);
            }
        }
        else if (isWildcardImport(importDeclaration)) {
            Identifier scope = fromIdentifierTSNode(scopeNode);
            return new ImportAll(scope);
        }
        else {
            Identifier scope = fromIdentifierTSNode(scopeNode.getChildByFieldName("scope"));
            Identifier member = fromIdentifierTSNode(scopeNode.getChildByFieldName("name"));
            return new ImportMembers(scope, member);
        }
    }

    private Node fromNullLiteralTSNode(TSNode nullLiteral) {
        return new NullLiteral();
    }

    private Node fromContinueStatementTSNode(TSNode continueNode) {
        return new ContinueStatement();
    }

    private Node fromBreakStatementTSNode(TSNode breakNode) {
        return new BreakStatement();
    }

    private CaseBlock fromSwitchGroupTSNode(TSNode switchGroup) {
        Expression matchValue =
                (Expression) fromTSNode(switchGroup.getNamedChild(0).getNamedChild(0));

        List<Node> statements = new ArrayList<>();
        for (int i = 1; i < switchGroup.getNamedChildCount(); i++) {
            statements.add(fromTSNode(switchGroup.getNamedChild(i)));
        }

        CaseBlock caseBlock;
        if (!statements.isEmpty() && statements.getLast() instanceof BreakStatement) {
            statements.removeLast();
            caseBlock = new BasicCaseBlock(matchValue, new CompoundStatement(statements));
        }
        else {
            caseBlock = new FallthroughCaseBlock(matchValue, new CompoundStatement(statements));
        }

        return caseBlock;
    }

    private Node fromSwitchExpressionTSNode(TSNode switchNode) {
        Expression matchValue =
                (Expression) fromTSNode(switchNode.getChildByFieldName("condition").getNamedChild(0));

        DefaultCaseBlock defaultCaseBlock = null;
        List<CaseBlock> cases = new ArrayList<>();

        TSNode switchBlock = switchNode.getChildByFieldName("body");
        for (int i = 0; i < switchBlock.getNamedChildCount(); i++) {
            TSNode switchGroup = switchBlock.getNamedChild(i);

            String labelName = getCodePiece(switchGroup.getNamedChild(0));
            if (labelName.equals("default")) {
                List<Node> statements = new ArrayList<>();

                for (int j = 1; j < switchGroup.getNamedChildCount(); j++) {
                    statements.add(fromTSNode(switchGroup.getNamedChild(j)));
                }

                if (!statements.isEmpty() && statements.getLast() instanceof BreakStatement) {
                    statements.removeLast();
                }
                defaultCaseBlock = new DefaultCaseBlock(new CompoundStatement(statements));
            }
            else {
                CaseBlock caseBlock = fromSwitchGroupTSNode(switchGroup);
                cases.add(caseBlock);
            }
        }

        return new SwitchStatement(matchValue, cases, defaultCaseBlock);
    }

    private MethodDefinition fromMethodDeclarationTSNode(TSNode node) {
        List<Modifier> modifiers = new ArrayList<>();
        if (node.getChild(0).getType().equals("modifiers")) {
            modifiers.addAll(fromModifiers(node.getChild(0)));
        }

        Type returnType = fromTypeTSNode(node.getChildByFieldName("type"));
        Identifier identifier = fromScopedIdentifierTSNode(node.getChildByFieldName("name"));
        List<DeclarationArgument> parameters = fromMethodParameters(node.getChildByFieldName("parameters"));

        // TODO: Пока не реализован механизм нахождения класса, к которому принадлежит метод, и определение аннотаций
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

        // Дети string_literal это либо string_fragment, либо escape_sequence,
        // либо multiline_string_fragment поэтому ничего экранировать не нужно,
        // они уже представлены так как надо
        for (int i = 0; i < node.getNamedChildCount(); i++) {
            TSNode child = node.getNamedChild(i);
            builder.append(getCodePiece(child));
        }

        return StringLiteral.fromUnescaped(builder.toString(), StringLiteral.Type.NONE);
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

    private List<TSNode> getChildrenByFieldName(TSNode node, String fieldName) {
        List<TSNode> nodes = new ArrayList<>();

        for (int i = 0; i < node.getChildCount(); i++) {
            String currentNodeFieldName = node.getFieldNameForChild(i);
            if (currentNodeFieldName != null && currentNodeFieldName.equals(fieldName)) {
                nodes.add(node.getChild(i));
            }
        }

        return nodes;
    }

    private AssignmentStatement assignmentExpressionToStatement(AssignmentExpression expression) {
        return new AssignmentStatement(expression.getLValue(), expression.getRValue());
    }

    @Nullable
    private RangeForLoop tryMakeRangeForLoop(HasInitialization init,
                                                Expression condition,
                                                Expression update,
                                                Statement body) {
        SimpleIdentifier loopVariable = null;
        Expression start = null;
        Expression stop = null;
        Expression step = null;
        boolean isExcludingEnd = false;

        if (init instanceof AssignmentExpression assignmentExpression
                && assignmentExpression.getLValue() instanceof SimpleIdentifier loopVariable_
                && assignmentExpression.getRValue() instanceof IntegerLiteral start_) {
            loopVariable = loopVariable_;
            start = start_;
        }
        // TODO: этот ужас нужно когда-нибудь переписать нормально
        else if (init instanceof VariableDeclaration variableDeclaration) {
            List<VariableDeclarator> declarators = List.of(variableDeclaration.getDeclarators());

            if (declarators.size() == 1) {
                VariableDeclarator declarator = declarators.getFirst();
                loopVariable = declarator.getIdentifier();

                Expression wrappedExpression = declarator.getRValue();
                if (wrappedExpression != null) {
                    if (wrappedExpression instanceof IntegerLiteral start_) {
                        start = start_;
                    }
                }
            }
        }

        if (condition instanceof BinaryComparison binaryComparison
            && binaryComparison.getLeft().equals(loopVariable)
            && binaryComparison.getRight() instanceof IntegerLiteral stop_) {
            stop = stop_;

            if (binaryComparison instanceof LtOp || binaryComparison instanceof GtOp) {
                isExcludingEnd = true;
            }
            else if (binaryComparison instanceof LeOp || binaryComparison instanceof GeOp) {
                isExcludingEnd = false;
            }
            else {
                // Т.к. binaryComparison может быть не только операцией больше/меньше, а еще
                // равно/не равно, то во втором случае нельзя организовать диапазон
                stop = null;
            }
        }

        step = switch (update) {
            case PostfixDecrementOp postfixDecrementOp -> new IntegerLiteral("-1");
            case PostfixIncrementOp postfixIncrementOp -> new IntegerLiteral("1");
            case PrefixDecrementOp prefixDecrementOp -> new IntegerLiteral("-1");
            case PrefixIncrementOp prefixIncrementOp -> new IntegerLiteral("1");
            default -> null;
        };

        if (start != null && stop != null && step != null && loopVariable != null) {
            Range range = new Range(start, stop, step, false, isExcludingEnd);
            return new RangeForLoop(range, loopVariable, body);
        }

        return null;
    }

    private Loop fromForStatementTSNode(TSNode node) {
        HasInitialization init = null;
        Expression condition = null;
        Expression update = null;

        if (!node.getChildByFieldName("init").isNull()) {
            List<TSNode> assignments = getChildrenByFieldName(node, "init");

            if (assignments.size() == 1) {
                init = (HasInitialization) fromTSNode(assignments.getFirst());
            }
            else if (assignments.size() > 1) {
                List<AssignmentStatement> assignmentStatements =
                        assignments.stream().map(
                                tsNode ->
                                        assignmentExpressionToStatement((AssignmentExpression) fromTSNode(tsNode))
                        ).toList();
                init = new MultipleAssignmentStatement(assignmentStatements);
            }
            else {
                throw new IllegalStateException("This should never occur");
            }
        }

        if (!node.getChildByFieldName("condition").isNull()) {
            condition = (Expression) fromTSNode(node.getChildByFieldName("condition"));
        }

        if (!node.getChildByFieldName("update").isNull()) {
            List<TSNode> updates = getChildrenByFieldName(node, "update");

            if (updates.size() == 1) {
                update = (Expression) fromTSNode(updates.getFirst());
            }
            else if (updates.size() > 1) {
                List<Expression> updateExpressions =
                        updates.stream().map(tsNode -> (Expression) fromTSNode(tsNode)).toList();
                update = new ExpressionSequence(updateExpressions);
            }
            else {
                throw new IllegalStateException("This should never occur");
            }
        }

        Statement body = (Statement) fromTSNode(node.getChildByFieldName("body"));

        if (init == null && condition == null && update == null) {
            return new InfiniteLoop(body);
        }

        RangeForLoop rangeFor = tryMakeRangeForLoop(init, condition, update, body);
        if (rangeFor != null) {
            return rangeFor;
        }

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

    private Type fromTypeTSNode(TSNode node) {
        String type = node.getType();
        String typeName = getCodePiece(node);
        Type parsedType = null;

        switch (type) {
            case "integral_type":
                parsedType = switch(typeName) {
                    case "char" -> new CharacterType();
                    case "int", "short", "long", "byte" -> new IntType();
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
                switch (typeName) {
                    case "String" -> parsedType = new StringType();
                    case "Object" -> parsedType = new UnknownType();
                    default -> {
                        if (!_userTypes.containsKey(typeName)) {
                            _userTypes.put(typeName, new Class(new SimpleIdentifier(typeName)));
                        }
                        parsedType = _userTypes.get(typeName);
                    }
                }
                break;
            case "scoped_type_identifier":
                throw new IllegalStateException("Scoped type identifiers are not supported yet");
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

        MethodDefinition mainMethod = classDefinition.findMethod("main");
        return (mainMethod != null) ? new ProgramEntryPoint(body, classDefinition, mainMethod)
                                    : new ProgramEntryPoint(body, classDefinition);
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

                MethodDefinition m = mainClass.findMethod("main");
                if (m != null) {
                    mainMethod = m;
                }
            }
        }

        return new ProgramEntryPoint(nodes, mainClass, mainMethod);
    }

    private Loop fromWhileTSNode(TSNode node) {
        TSNode tsCond = node.getChildByFieldName("condition");
        Expression mtCond = (Expression) fromTSNode(tsCond);

        TSNode tsBody = node.getChildByFieldName("body");
        Statement mtBody = (Statement) fromTSNode(tsBody);

        if (mtCond instanceof BoolLiteral boolLiteral && boolLiteral.getValue()) {
            return new InfiniteLoop(mtBody);
        }

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

        TSNode alternativeNode = node.getChildByFieldName("alternative");
        if (alternativeNode.isNull()) {
            return new IfStatement(condition, consequence);
        }

        Statement alternative = (Statement) fromTSNode(alternativeNode);
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
            case "~" -> new InversionOp(argument);
            case "-" -> new UnaryMinusOp(argument);
            case "+" -> new UnaryPlusOp(argument);
            default -> throw new UnsupportedOperationException();
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

}
