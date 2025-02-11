package org.vstu.meaningtree.languages;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.treesitter.*;
import org.vstu.meaningtree.MeaningTree;
import org.vstu.meaningtree.exceptions.MeaningTreeException;
import org.vstu.meaningtree.exceptions.UnsupportedParsingException;
import org.vstu.meaningtree.nodes.*;
import org.vstu.meaningtree.nodes.declarations.FunctionDeclaration;
import org.vstu.meaningtree.nodes.declarations.SeparatedVariableDeclaration;
import org.vstu.meaningtree.nodes.declarations.VariableDeclaration;
import org.vstu.meaningtree.nodes.declarations.components.VariableDeclarator;
import org.vstu.meaningtree.nodes.definitions.FunctionDefinition;
import org.vstu.meaningtree.nodes.enums.AugmentedAssignmentOperator;
import org.vstu.meaningtree.nodes.expressions.BinaryExpression;
import org.vstu.meaningtree.nodes.expressions.Identifier;
import org.vstu.meaningtree.nodes.expressions.ParenthesizedExpression;
import org.vstu.meaningtree.nodes.expressions.UnaryExpression;
import org.vstu.meaningtree.nodes.expressions.bitwise.*;
import org.vstu.meaningtree.nodes.expressions.calls.FunctionCall;
import org.vstu.meaningtree.nodes.expressions.calls.MethodCall;
import org.vstu.meaningtree.nodes.expressions.comparison.*;
import org.vstu.meaningtree.nodes.expressions.identifiers.QualifiedIdentifier;
import org.vstu.meaningtree.nodes.expressions.identifiers.ScopedIdentifier;
import org.vstu.meaningtree.nodes.expressions.identifiers.SelfReference;
import org.vstu.meaningtree.nodes.expressions.identifiers.SimpleIdentifier;
import org.vstu.meaningtree.nodes.expressions.literals.*;
import org.vstu.meaningtree.nodes.expressions.logical.NotOp;
import org.vstu.meaningtree.nodes.expressions.logical.ShortCircuitAndOp;
import org.vstu.meaningtree.nodes.expressions.logical.ShortCircuitOrOp;
import org.vstu.meaningtree.nodes.expressions.math.*;
import org.vstu.meaningtree.nodes.expressions.newexpr.ArrayNewExpression;
import org.vstu.meaningtree.nodes.expressions.newexpr.ObjectNewExpression;
import org.vstu.meaningtree.nodes.expressions.newexpr.PlacementNewExpression;
import org.vstu.meaningtree.nodes.expressions.other.*;
import org.vstu.meaningtree.nodes.expressions.pointers.PointerMemberAccess;
import org.vstu.meaningtree.nodes.expressions.pointers.PointerPackOp;
import org.vstu.meaningtree.nodes.expressions.pointers.PointerUnpackOp;
import org.vstu.meaningtree.nodes.expressions.unary.*;
import org.vstu.meaningtree.nodes.io.*;
import org.vstu.meaningtree.nodes.memory.MemoryAllocationCall;
import org.vstu.meaningtree.nodes.memory.MemoryFreeCall;
import org.vstu.meaningtree.nodes.statements.CompoundStatement;
import org.vstu.meaningtree.nodes.statements.ExpressionStatement;
import org.vstu.meaningtree.nodes.types.GenericUserType;
import org.vstu.meaningtree.nodes.types.NoReturn;
import org.vstu.meaningtree.nodes.types.UnknownType;
import org.vstu.meaningtree.nodes.types.UserType;
import org.vstu.meaningtree.nodes.types.builtin.*;
import org.vstu.meaningtree.nodes.types.containers.*;
import org.vstu.meaningtree.nodes.types.containers.components.Shape;
import org.vstu.meaningtree.nodes.types.user.Class;
import org.vstu.meaningtree.nodes.types.user.GenericClass;
import org.vstu.meaningtree.utils.BodyBuilder;
import org.vstu.meaningtree.utils.env.SymbolEnvironment;

import java.util.*;

public class CppLanguage extends LanguageParser {
    private final TSLanguage _language;
    private final TSParser _parser;
    private final Map<String, UserType> _userTypes;

    private int binaryRecursiveFlag = -1;

    public CppLanguage() {
        _language = new TreeSitterCpp();
        _parser = new TSParser();
        _parser.setLanguage(_language);
        _userTypes = new HashMap<>();
    }

    @Override
    public TSTree getTSTree() {
        TSTree tree = _parser.parseString(null, _code);
        /*
        TODO: only for test
        try {
            tree.printDotGraphs(new File("TSTree.dot"));
        } catch (IOException e) { }
        */
        return tree;
    }

    @NotNull
    public synchronized MeaningTree getMeaningTree(String code) {
        _code = code;
        TSNode rootNode = getRootNode();
        List<String> errors = lookupErrors(rootNode);
        if (!errors.isEmpty() && !getConfigParameter("skipErrors").getBooleanValue()) {
            throw new MeaningTreeException(String.format("Given code has syntax errors: %s", errors));
        }
        Node node = fromTSNode(rootNode);
        if (node instanceof AssignmentExpression expr) {
            node = expr.toStatement();
        }
        return new MeaningTree(node);
    }

    @Override
    public TSNode getRootNode() {
        TSNode result = super.getRootNode();
        if (getConfigParameter("expressionMode").getBooleanValue()) {
            // В режиме выражений в код перед парсингом подставляется заглушка в виде точки входа, чтобы парсинг выражения был корректен (имел контекст внутри функции)
            TSNode func = result.getNamedChild(0);
            assert func.getType().equals("function_definition");
            assert (getCodePiece(func.getChildByFieldName("declarator")
                    .getChildByFieldName("declarator"))
                    .equals("main"));
            TSNode body = func.getChildByFieldName("body");
            if (body.getNamedChildCount() > 1 && !body.getNamedChild(0).isError()) {
                throw new UnsupportedParsingException("Many expressions in given code (you're using expression mode)");
            }
            if (body.getNamedChildCount() < 1) {
                throw new UnsupportedParsingException("Main expression was not found in expression mode");
            }
            result = body.getNamedChild(0);
            if (result.getType().equals("expression_statement")) {
                result = result.getNamedChild(0);
            }
        }
        return result;
    }

    @NotNull
    private Node fromTSNode(@NotNull TSNode node) {
        Objects.requireNonNull(node);

        Node createdNode = switch (node.getType()) {
            case "ERROR", "parameter_pack_expansion" -> fromTSNode(node.getNamedChild(0));
            case "translation_unit" -> fromTranslationUnit(node);
            case "function_definition" -> fromFunction(node);
            case "expression_statement"-> fromExpressionStatement(node);
            case "binary_expression" -> fromBinaryExpression(node);
            case "unary_expression" -> fromUnaryExpression(node);
            case "parenthesized_expression" -> fromParenthesizedExpression(node);
            case "update_expression" -> fromUpdateExpression(node);
            case "call_expression" -> fromCallExpression(node);
            case "conditional_expression" -> fromConditionalExpression(node);
            case "comma_expression" -> fromCommaExpression(node);
            case "subscript_expression" -> fromSubscriptExpression(node);
            case "assignment_expression" -> fromAssignmentExpression(node);
            case "compound_literal_expression" -> fromTSNode(node.getChildByFieldName("value"));
            case "declaration" -> fromDeclaration(node);
            case "identifier", "qualified_identifier", "field_expression", "namespace_identifier", "type_identifier", "field_identifier" -> fromIdentifier(node);
            case "number_literal" -> fromNumberLiteral(node);
            case "char_literal" -> fromCharLiteral(node);
            case "string_literal" -> fromStringLiteral(node);
            case "user_defined_literal" -> fromUserDefinedLiteral(node);
            case "null" -> new NullLiteral();
            case "true" -> new BoolLiteral(true);
            case "concatenated_string" -> fromConcatenatedString(node);
            case "false" -> new BoolLiteral(false);
            case "initializer_list" -> fromInitializerList(node);
            case "primitive_type", "template_function", "placeholder_type_specifier", "sized_type_specifier", "type_descriptor" -> fromType(node);
            case "sizeof_expression" -> fromSizeOf(node);
            case "compound_statement" -> fromBody(node);
            case "new_expression" -> fromNewExpression(node);
            case "delete_expression" -> fromDeleteExpression(node);
            case "cast_expression" -> fromCastExpression(node);
            case "pointer_expression" -> fromPointerExpression(node);
            case "this" -> new SelfReference("this");
            case "offsetof_expression" -> fromOffsetOf(node);
            case "comment" -> fromComment(node);
            default -> throw new UnsupportedParsingException(String.format("Can't parse %s this code:\n%s", node.getType(), getCodePiece(node)));
        };
        assignValue(node, createdNode);
        return createdNode;
    }

    private Node fromConcatenatedString(TSNode node) {
        List<StringLiteral> literals = new ArrayList<>();
        for (int i = 0; i < node.getNamedChildCount(); i++) {
            literals.add(fromStringLiteral(node.getNamedChild(i)));
        }
        StringBuilder val = new StringBuilder();
        for (StringLiteral s : literals) {
            val.append(s.getUnescapedValue());
        }
        return StringLiteral.fromUnescaped(val.toString(), StringLiteral.Type.NONE);
    }

    private Comment fromComment(TSNode node) {
        return Comment.fromUnescaped(getCodePiece(node).replaceFirst("/*", "")
                .replaceFirst("//", "").replace("*/", "").trim());
    }

    private Node fromOffsetOf(TSNode node) {
        return new FunctionCall(new SimpleIdentifier("offsetof"), (Expression) fromTSNode(node.getChildByFieldName("type").getChildByFieldName("type")),
                (Expression) fromTSNode(node.getChildByFieldName("member")));
    }

    private Node fromCharLiteral(TSNode node) {
        return new CharacterLiteral(getCodePiece(node.getNamedChild(0)).charAt(0));
    }

    private Node fromFunction(TSNode node) {
        // TODO: требуется ревизия. Сделано только для режима выражений и функции main
        Type retType = fromType(node.getChildByFieldName("type"));
        TSNode declarator = node.getChildByFieldName("declarator");
        SimpleIdentifier name = (SimpleIdentifier) fromTSNode(declarator.getChildByFieldName("declarator"));
        CompoundStatement body = fromBody(node.getChildByFieldName("body"));
        // TODO: Аргументы игнорируются!!
        return new FunctionDefinition(new FunctionDeclaration(name, retType, List.of(), List.of()), body);
    }

    private CompoundStatement fromBody(TSNode node) {
        // TODO: Нужна поддержка таблицы символов
        BodyBuilder builder = new BodyBuilder();
        for (int i = 1; i < node.getChildCount() - 1; i++) {
            Node child = fromTSNode(node.getChild(i));
            builder.put(child);
        }
        return builder.build();
    }

    private Node fromInitializerList(TSNode node) {
        List<Expression> expressions = new ArrayList<>();
        for (int i = 0; i < node.getNamedChildCount(); i++) {
            expressions.add((Expression) fromTSNode(node.getNamedChild(i)));
        }
        return new ArrayLiteral(expressions);
    }

    private Node fromPointerExpression(TSNode node) {
        String op = getCodePiece(node);
        Expression argument = (Expression) fromTSNode(node.getChildByFieldName("argument"));
        if (op.startsWith("&")) {
            if (argument instanceof AddOp binOp) {
                Expression leftmost = binOp.getLeft();
                List<Expression> args = new ArrayList<>();
                args.add(binOp.getRight());
                while (leftmost instanceof AddOp leftmostOp) {
                    leftmost = leftmostOp.getLeft();
                    args.add(leftmostOp.getRight());
                }
                return new IndexExpression(leftmost, BinaryExpression.
                        fromManyOperands(args.reversed().toArray(new Expression[0]), 0, AddOp.class), true);
            }
            return new PointerPackOp(argument);
        } else if (op.startsWith("*")) {
            return new PointerUnpackOp(argument);
        } else {
            throw new MeaningTreeException("Unknown pointer expression: ".concat(op));
        }
    }

    private Node fromCastExpression(TSNode node) {
        Type type = fromType(node.getChildByFieldName("type"));
        Expression value = (Expression) fromTSNode(node.getChildByFieldName("value"));
        if (value instanceof ParenthesizedExpression p && p.getExpression() instanceof DivOp div && type instanceof IntType) {
            return new FloorDivOp(div.getLeft(), div.getRight());
        }
        return new CastTypeExpression(type, value);
    }

    private Node fromDeleteExpression(TSNode node) {
        String line = getCodePiece(node);
        return new DeleteExpression((Expression) fromTSNode(node.getNamedChild(0)), line.contains("[") && line.contains("]"));
    }

    private Node fromNewExpression(TSNode node) {
        Type type = fromType(node.getChildByFieldName("type"));

        TSNode placement = node.getChildByFieldName("placement");
        TSNode declarator = node.getChildByFieldName("declarator");
        TSNode arguments = node.getChildByFieldName("arguments");

        List<Expression> args = new ArrayList<>();
        TSNode childSource;
        if (!placement.isNull()) {
            childSource = placement;
        } else if (!arguments.isNull()) {
            childSource = arguments;
        } else if (!declarator.isNull()) {
            List<Expression> initList = new ArrayList<>();
            if (!arguments.isNull()) {
                for (int i = 0; i < arguments.getNamedChildCount(); i++) {
                    initList.add((Expression) fromTSNode(arguments.getNamedChild(i)));
                }
            }
            List<Expression> dimensions = new ArrayList<>();
            dimensions.add((Expression) fromTSNode(declarator.getNamedChild(0)));
            while (!declarator.getNamedChild(1).isNull()
                    && declarator.getNamedChild(1).getType().equals("new_declarator")) {
                declarator = declarator.getNamedChild(1);
                dimensions.add((Expression) fromTSNode(declarator.getNamedChild(0)));
            }
            ArrayInitializer initializer = !initList.isEmpty() ? new ArrayInitializer(initList) : null;
            return new ArrayNewExpression(type, new Shape(dimensions.size(), dimensions.toArray(new Expression[0])), initializer);
        } else {
            throw new MeaningTreeException("No arguments for new expression");
        }
        for (int i = 0; i < childSource.getNamedChildCount(); i++) {
            args.add((Expression) fromTSNode(childSource.getNamedChild(i)));
        }
        if (childSource == placement) {
            return new PlacementNewExpression(type, args);
        } else {
            return new ObjectNewExpression(type, args);
        }
    }

    private Node fromSizeOf(TSNode node) {
        TSNode inner = node.getChildByFieldName("value");
        if (inner.isNull()) {
            inner = node.getChildByFieldName("type");
        }
        return new SizeofExpression((Expression) fromTSNode(inner));
    }

    private Type fromTypeByString(String type) {
        return switch (type) {
            case "int" -> new IntType();
            case "int8_t" -> new IntType(8);
            case "uint8_t" -> new IntType(8, true);
            case "size_t", "uint64_t" -> new IntType(64, true);
            case "int16_t" -> new IntType(16);
            case "int32_t", "time32_t" -> new IntType(32);
            case "int64_t", "time64_t" -> new IntType(64);
            case "uint16_t" -> new IntType(16, true);
            case "uint32_t" -> new IntType(32, true);
            case "double" -> new FloatType(64);
            case "float" -> new FloatType(32);
            case "char" -> new CharacterType(8);
            case "wchar_t", "char16_t" -> new CharacterType(16);
            case "bool" -> new BooleanType();
            case "void" -> new NoReturn();
            case "string" -> new StringType(8);
            case "wstring", "u16string" -> new StringType(16);
            case "u32string" -> new StringType(32);
            // TODO: add support for symbol table
            default -> new Class(new SimpleIdentifier(type));
        };
    }

    private String reprQualifiedIdentifier(QualifiedIdentifier ident) {
        if (ident.getScope() instanceof QualifiedIdentifier leftQualified) {
            return String.format("%s::%s", reprQualifiedIdentifier(leftQualified), ident.getMember().toString());
        } else if (ident.getScope() instanceof ScopedIdentifier scoped) {
            return String.format("%s::%s", String.join(".",
                    scoped.getScopeResolution().stream().map(Expression::toString).toList()), ident.getMember().toString());
        }
        return String.format("%s::%s", ident.getScope().toString(), ident.getMember().toString());
    }

    private Type fromType(TSNode node) {
        String type = getCodePiece(node);
        if (node.getType().equals("type_identifier") || node.getType().equals("primitive_type")) {
            return fromTypeByString(type);
        }
        else if (node.getType().equals("sized_type_specifier")) {
            return parseSizedTypeSpecifier(node);
        }
        else if (node.getType().equals("type_descriptor")) {
            Type inner;
            if (node.getChildByFieldName("type").getType().equals("sized_type_specifier")) {
                inner = parseSizedTypeSpecifier(node.getChildByFieldName("type"));
            } else {
                inner = fromType(node.getChildByFieldName("type"));
            }
            if (!node.getChildByFieldName("declarator").isNull()
                    && node.getChildByFieldName("declarator").getType().equals("abstract_pointer_declarator")) {
                if (inner instanceof NoReturn) {
                    return new PointerType(new UnknownType());
                }
                return new PointerType(inner);
            } else if (!node.getChildByFieldName("declarator").isNull()
                    && node.getChildByFieldName("declarator").getType().equals("abstract_reference_declarator")) {
                return new ReferenceType(inner);
            }
            return inner;
        } else if (node.getType().equals("template_function")) {
            // TODO: add support for symbol table
            Identifier ident = (Identifier) fromIdentifier(node.getChildByFieldName("name"));
            List<Type> subTypes = new ArrayList<>();
            TSNode arguments = node.getChildByFieldName("arguments");
            for (int i = 0; i < arguments.getNamedChildCount(); i++) {
                subTypes.add(fromType(arguments.getNamedChild(i)));
            }
            return new GenericClass(ident, subTypes.toArray(new Type[0]));
        } else if (node.getType().equals("qualified_identifier")) {
            QualifiedIdentifier q;
            List<Type> generic = new ArrayList<>();
            if (node.getChildByFieldName("name").getType().equals("template_type")) {
                TSNode template = node.getChildByFieldName("name");
                SimpleIdentifier s = new SimpleIdentifier(getCodePiece(template.getChildByFieldName("name")));
                TSNode arguments = template.getChildByFieldName("arguments");
                for (int i = 0; i < arguments.getNamedChildCount(); i++) {
                    generic.add(fromType(arguments.getNamedChild(i)));
                }
                q = new QualifiedIdentifier((Identifier) fromIdentifier(node.getChildByFieldName("scope")), s);
            } else {
                q = (QualifiedIdentifier) fromIdentifier(node);
            }
            Type type1 = !generic.isEmpty() ? generic.getFirst() : new UnknownType();
            Type type2 = generic.size() > 1 ? generic.get(1) : new UnknownType();
            return switch (reprQualifiedIdentifier(q)) {
                case "std::map" -> new DictionaryType(type1, type2);
                case "std::list", "std::vector", "std::array" -> new ListType(type1);
                case "std::set" -> new SetType(type1);
                case "std::string", "std::wstring" -> new StringType(8);
                case "std::u16string" -> new StringType(16);
                case "std::u32string" -> new StringType(32);
                default -> {
                    // TODO: add support for symbol table
                    if (generic.isEmpty()) {
                        yield new Class(q);
                    }
                    yield new GenericClass(q, generic.toArray(new Type[0]));
                }
            };

        } else {
            return new UnknownType();
        }
    }

    private Type parseSizedTypeSpecifier(TSNode node) {
        String type = getCodePiece(node);
        String subType = node.getChildByFieldName("type").isNull() ? "int" : getCodePiece(node.getChildByFieldName("type"));

        if (type.matches(".*(long|int|short|unsigned|signed).*")) {
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
            if (size > 64) {
                size = 64;
            }
            if (subType.equals("int") || subType.equals("short") || subType.equals("long")) {
                return new IntType(size, isUnsigned);
            } else if (subType.equals("char")) {
                return new CharacterType();
            } else {
                return new FloatType(size);
            }
        } else {
            throw new UnsupportedOperationException(String.format("Can't parse sized type %s this code:\n%s", node.getType(), getCodePiece(node)));
        }
    }

    private StringLiteral fromStringLiteral(TSNode node) {
        String strLiteral = getCodePiece(node);
        boolean isWide = strLiteral.toLowerCase().startsWith("l");
        strLiteral = strLiteral.substring(1, strLiteral.length() - 1);
        StringLiteral literal = StringLiteral.fromEscaped(strLiteral, StringLiteral.Type.NONE);
        if (isWide) {
            literal.setTypeCharSize(32);
        }
        return literal;
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

        return new CommaExpression(expressions);
    }

    @NotNull
    private TernaryOperator fromConditionalExpression(@NotNull TSNode node) {
        Expression condition = (Expression) fromTSNode(node.getChildByFieldName("condition"));
        Expression consequence = (Expression) fromTSNode(node.getChildByFieldName("consequence"));
        Expression alternative = (Expression) fromTSNode(node.getChildByFieldName("alternative"));
        return new TernaryOperator(condition, consequence, alternative);
    }

    public Expression sanitizeFromStd(Expression expr) {
        if (expr instanceof QualifiedIdentifier qual && qual.getScope().equalsIdentifier("std")) {
            return qual.getMember();
        }
        return expr;
    }

    @NotNull
    private Node fromCallExpression(@NotNull TSNode node) {
        Expression functionName = (Expression) fromTSNode(node.getChildByFieldName("function"));
        Expression clearFunctionName = sanitizeFromStd(functionName);

        TSNode tsArguments = node.getChildByFieldName("arguments");
        List<Expression> arguments = new ArrayList<>();
        for (int i = 0; i < tsArguments.getNamedChildCount(); i++) {
            TSNode tsArgument = tsArguments.getNamedChild(i);
            Expression argument = (Expression) fromTSNode(tsArgument);
            arguments.add(argument);
        }

        if (functionName instanceof ParenthesizedExpression p
                && p.getExpression() instanceof SimpleIdentifier ident
                && arguments.size() == 1) {
            return new CastTypeExpression(new Class(ident), arguments.getFirst());
        }

        if (clearFunctionName.toString().equals("pow") && arguments.size() == 2) {
            return new PowOp(arguments.getFirst(), arguments.getLast());
        }

        if (functionName instanceof ScopedIdentifier scoped && scoped.getScopeResolution().size() > 1) {
            List<SimpleIdentifier> object = scoped.getScopeResolution()
                    .subList(0, scoped.getScopeResolution().size() - 1);
            return new MethodCall(object.size() == 1 ? object.getFirst() : new ScopedIdentifier(object)
                    , scoped.getScopeResolution().getLast(), arguments);
        }
        if (functionName instanceof MemberAccess memAccess) {
            return new MethodCall(memAccess.getExpression(), memAccess.getMember(), arguments);
        }

        if (clearFunctionName.toString().equals("printf")) {
            return new FormatPrint(arguments.getFirst(), arguments.subList(1, arguments.size()).toArray(new Expression[0]));
        }

        if (functionName.toString().equals("scanf") || clearFunctionName.toString().equals("scanf_s")) {
            return new FormatInput(arguments.getFirst(), arguments.subList(1, arguments.size()).toArray(new Expression[0]));
        }

        if ((clearFunctionName.toString().equals("puts") || clearFunctionName.toString().equals("puts_s")) && arguments.size() == 1) {
            return new PrintValues(arguments,
                    StringLiteral.fromUnescaped("", StringLiteral.Type.NONE),
                    StringLiteral.fromUnescaped("", StringLiteral.Type.NONE));
        }

        if (clearFunctionName.toString().equals("gets") || clearFunctionName.toString().equals("gets_s")) {
            return new PointerInputCommand(arguments.getFirst(), arguments.subList(1, arguments.size()));
        }

        if ((clearFunctionName.toString().equals("malloc") || clearFunctionName.toString().equals("сalloc") ||
                clearFunctionName.toString().equals("_malloc") || clearFunctionName.toString().equals("_сalloc"))
                && arguments.size() == 1) {
            Type foundType = null;
            Expression count = new IntegerLiteral(1);
            for (Expression arg : arguments) {
                if (arg instanceof SizeofExpression sizeOf && sizeOf.getExpression() instanceof Type type) {
                    foundType = type;
                } else if (arg instanceof MulOp mulOp) {
                    if (mulOp.getLeft() instanceof SizeofExpression sizeOf && sizeOf.getExpression() instanceof Type type) {
                        foundType = type;
                    }
                    if (!(mulOp.getRight() instanceof SizeofExpression)) {
                        count = mulOp.getRight();
                    }

                    if (mulOp.getRight() instanceof SizeofExpression sizeOf && sizeOf.getExpression() instanceof Type type) {
                        foundType = type;
                    }
                    if (!(mulOp.getLeft() instanceof SizeofExpression)) {
                        count = mulOp.getLeft();
                    }
                }
            }
            if (foundType != null) {
                return new MemoryAllocationCall(foundType, count, functionName.toString().equals("сalloc"));
            }
        }

        if (functionName.toString().equals("free") && arguments.size() == 1) {
            return new MemoryFreeCall(arguments.getFirst());
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
        Expression argument = (Expression) fromTSNode(node.getChildByFieldName("argument"));
        return switch (getCodePiece(node.getChild(0))) {
            case "!", "not" -> new NotOp(argument);
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
            return new PostfixIncrementOp((Expression) fromTSNode(node.getChild(0)));
        }
        else if (code.startsWith("++")) {
            return new PrefixIncrementOp((Expression) fromTSNode(node.getChild(1)));
        }
        else if (code.endsWith("--")) {
            return new PostfixDecrementOp((Expression) fromTSNode(node.getChild(0)));
        }
        else if (code.startsWith("--")) {
            return new PrefixDecrementOp((Expression) fromTSNode(node.getChild(1)));
        }

        throw new IllegalArgumentException();
    }

    @NotNull
    private Node fromBinaryExpression(@NotNull TSNode node) {
        if (binaryRecursiveFlag == -1) {
            binaryRecursiveFlag = node.getEndByte();
        }
        Expression left = (Expression) fromTSNode(node.getChildByFieldName("left"));
        Expression right = (Expression) fromTSNode(node.getChildByFieldName("right"));
        TSNode operator = node.getChildByFieldName("operator");
        if (binaryRecursiveFlag == node.getEndByte()) {
            binaryRecursiveFlag = -1;
        }

        return switch (getCodePiece(operator)) {
            case "+" -> new AddOp(left, right);
            case "-" -> new SubOp(left, right);
            case "*" -> new MulOp(left, right);
            case "/" -> new DivOp(left, right);
            case "%" -> new ModOp(left, right);
            case "<" -> new LtOp(left, right);
            case ">" -> new GtOp(left, right);
            case "==" -> {
                EqOp eq = new EqOp(left, right);
                if (eq.getLeft() instanceof FunctionCall call
                        && call.getArguments().size() == 1
                        && call.getFunction() instanceof GenericUserType type
                        && type.getTypeParameters().length == 1
                        && type.getName().toString().equals("dynamic_cast")
                        && right instanceof NullLiteral
                ) {
                    yield new NotOp(new ParenthesizedExpression(new InstanceOfOp(call.getArguments().getFirst(), type.getTypeParameters()[0])));
                }

                if (eq.getLeft() instanceof PointerPackOp leftPtr && eq.getRight() instanceof PointerPackOp rightPtr ) {
                    yield new ReferenceEqOp(leftPtr.getArgument(), rightPtr.getArgument(), false);
                }
                // TODO: add type checking

                yield eq;
            }
            case "!=" -> {
                NotEqOp neq = new NotEqOp(left, right);
                if (neq.getLeft() instanceof FunctionCall call
                        && call.getArguments().size() == 1
                        && call.getFunction() instanceof GenericUserType type
                        && type.getTypeParameters().length == 1
                        && type.getName().toString().equals("dynamic_cast")
                        && right instanceof NullLiteral
                ) {
                    yield new InstanceOfOp(call.getArguments().getFirst(), type.getTypeParameters()[0]);
                }
                if (neq.getLeft() instanceof PointerPackOp leftPtr && neq.getRight() instanceof PointerPackOp rightPtr) {
                    yield new ReferenceEqOp(leftPtr.getArgument(), rightPtr.getArgument(), false);
                }
                yield neq;
            }
            case ">=" -> new GeOp(left, right);
            case "<=" -> new LeOp(left, right);
            case "&&", "and" -> new ShortCircuitAndOp(left, right);
            case "||", "or" -> new ShortCircuitOrOp(left, right);
            case "&" -> new BitwiseAndOp(left, right);
            case "|" -> new BitwiseOrOp(left, right);
            case "^" -> new XorOp(left, right);
            case "<<" -> {
                LeftShiftOp lshift = new LeftShiftOp(left, right);
                if (binaryRecursiveFlag == -1) {
                    Expression fName = lshift.getLeftmost();
                    List<Expression> exprs = lshift.getRecursivePlainOperands();
                    boolean isEndl = sanitizeFromStd(exprs.getLast()).equalsIdentifier("endl");
                    if (sanitizeFromStd(fName).equalsIdentifier("cout")) {
                        yield new PrintValues(exprs.subList(1, exprs.size() - (isEndl ? 1 : 0)),
                                StringLiteral.fromUnescaped("", StringLiteral.Type.NONE),
                                StringLiteral.fromUnescaped(isEndl ? "\n" : "", StringLiteral.Type.NONE)
                        );
                    } else if (sanitizeFromStd(fName).equalsIdentifier("cin")) {
                        yield new InputCommand(exprs.subList(1, exprs.size()));
                    }
                }
                yield lshift;
            }
            case ">>" -> new RightShiftOp(left, right);
            case "<=>" -> new ThreeWayComparisonOp(left, right);
            default -> throw new UnsupportedOperationException(String.format("Can't parse operator %s", getCodePiece(operator)));
        };
    }



    @NotNull
    private Declaration fromDeclaration(@NotNull TSNode node) {
        TSNode typeNode = node.getChildByFieldName("type");
        Type mainType = fromType(typeNode);

        int i = 0;

        while (!"type".equals(node.getFieldNameForChild(i))) {
            TSNode currentNode = node.getNamedChild(i);
            if (currentNode.getType().equals("type_qualifier") && getCodePiece(currentNode).equals("const")) {
                mainType.setConst(true);
            }
            i++;
        }

        var declarators = new ArrayList<VariableDeclaration>();
        for (i += 1; i < node.getNamedChildCount(); i++) {
            TSNode tsDeclarator = node.getNamedChild(i);

            if (tsDeclarator.getType().equals("type_qualifier") && getCodePiece(tsDeclarator).equals("const")) {
                mainType.setConst(true);
            } else if (tsDeclarator.getType().equals("array_declarator")) {
                List<Expression> dimensions = new ArrayList<>();
                TSNode arrayDimension = tsDeclarator;
                while (!arrayDimension.isNull() && arrayDimension.getType().equals("array_declarator")) {
                    if (!arrayDimension.getChildByFieldName("value").isNull()) {
                        dimensions.add((Expression) fromTSNode(arrayDimension.getChildByFieldName("value")));
                    } else {
                        dimensions.add(null);
                    }
                    arrayDimension = arrayDimension.getChildByFieldName("declarator");
                }
                mainType = new ArrayType(mainType, dimensions.size(), dimensions);
                declarators.add(new VariableDeclaration(mainType, new VariableDeclarator((SimpleIdentifier) fromTSNode(arrayDimension))));
            } else if (tsDeclarator.getType().equals("init_declarator")) {
                TSNode tsVariableName = tsDeclarator.getChildByFieldName("declarator");
                Type type = mainType;

                if (tsVariableName.getType().equals("pointer_declarator")) {
                    type = new PointerType(mainType);
                    if (mainType instanceof NoReturn) {
                        type = new PointerType(new UnknownType());
                    }
                    if ((tsVariableName.getNamedChild(0).getType().equals("type_qualifier") &&
                            getCodePiece(tsVariableName.getNamedChild(0)).equals("const"))
                    ) {
                        type.setConst(true);
                    }
                    tsVariableName = tsVariableName.getChildByFieldName("declarator");
                } else if (tsVariableName.getType().equals("reference_declarator")) {
                    type = new ReferenceType(mainType);
                    tsVariableName = tsVariableName.getNamedChild(0);
                } else if (tsVariableName.getType().equals("array_declarator")) {
                    List<Expression> dimensions = new ArrayList<>();
                    TSNode arrayDimension = tsVariableName;
                    while (!arrayDimension.isNull() && arrayDimension.getType().equals("array_declarator")) {
                        if (!arrayDimension.getChildByFieldName("value").isNull()) {
                            dimensions.add((Expression) fromTSNode(arrayDimension.getChildByFieldName("value")));
                        } else {
                            dimensions.add(null);
                        }
                        arrayDimension = arrayDimension.getChildByFieldName("declarator");
                    }
                    type = new ArrayType(mainType, dimensions.size(), dimensions);
                    tsVariableName = arrayDimension;
                }
                TSNode tsValue = tsDeclarator.getChildByFieldName("value");

                SimpleIdentifier variableName = (SimpleIdentifier) fromTSNode(tsVariableName);
                Expression value = (Expression) fromTSNode(tsValue);
                if (value instanceof PlainCollectionLiteral col) {
                    if (mainType instanceof PlainCollectionType arrayType) {
                        col.setTypeHint(arrayType.getItemType());
                    } else {
                        col.setTypeHint(mainType);
                    }
                }

                VariableDeclarator declarator = new VariableDeclarator(variableName, value);
                declarators.add(new VariableDeclaration(type, declarator));
            } else {
                Type type = mainType;

                if (tsDeclarator.getType().equals("pointer_declaration")) {
                    type = new PointerType(mainType);
                    if (mainType instanceof NoReturn) {
                        type = new PointerType(new UnknownType());
                    }
                } else if (tsDeclarator.getType().equals("reference_declaration")) {
                    type = new ReferenceType(mainType);
                }
                declarators.add(new VariableDeclaration(type, new VariableDeclarator((SimpleIdentifier) fromIdentifier(tsDeclarator))));
            }
        }

        SeparatedVariableDeclaration sepDecl = new SeparatedVariableDeclaration(declarators);
        if (sepDecl.canBeReduced()) {
            return sepDecl.reduce();
        }
        return sepDecl;
    }

    private QualifiedIdentifier rightToLeftQualified(Identifier left, Identifier right) {
        if (right instanceof QualifiedIdentifier rightQualified) {
            SimpleIdentifier ident = (SimpleIdentifier) rightQualified.getScope();
            QualifiedIdentifier newLeft = new QualifiedIdentifier(left, ident);
            return rightToLeftQualified(newLeft, rightQualified.getMember());
        }
        return new QualifiedIdentifier(left, (SimpleIdentifier) right);
    }

    @NotNull
    private Node fromIdentifier(@NotNull TSNode node) {
        if (node.getType().equals("identifier") || node.getType().equals("field_identifier") || node.getType().equals("namespace_identifier") || node.getType().equals("type_identifier")) {
            return new SimpleIdentifier(getCodePiece(node));
        } else if (node.getType().equals("qualified_identifier")) {
            Identifier right = (Identifier) fromIdentifier(node.getChildByFieldName("name"));
            SimpleIdentifier left = (SimpleIdentifier) fromIdentifier(node.getChildByFieldName("scope"));
            return rightToLeftQualified(left, right);
        } else if (node.getType().equals("field_expression")) {
            Node treeNode = fromTSNode(node.getChildByFieldName("argument"));
            boolean isPointer = node.getChild(1).getType().equals("->");
            if (treeNode instanceof SimpleIdentifier ident && !isPointer) {
                return new MemberAccess(ident, (SimpleIdentifier) fromIdentifier(node.getChildByFieldName("field")));
            } else if (treeNode instanceof ScopedIdentifier ident && !isPointer) {
                List<SimpleIdentifier> identList = new ArrayList<>(ident.getScopeResolution());
                Identifier fieldIdent = (Identifier) fromIdentifier(node.getChildByFieldName("field"));
                if (fieldIdent instanceof SimpleIdentifier sIdent) {
                    identList.add(sIdent);
                } else if (fieldIdent instanceof ScopedIdentifier scopedIdent) {
                    identList.addAll(scopedIdent.getScopeResolution());
                } else if (fieldIdent instanceof QualifiedIdentifier) {
                    throw new MeaningTreeException("Unsupported scoped and qualified identifier combination");
                }
                return new ScopedIdentifier(identList).toMemberAccess();
            } else {
                if (isPointer) {
                    return new PointerMemberAccess((Expression) fromTSNode(node.getChildByFieldName("argument")), (SimpleIdentifier) fromTSNode(node.getChildByFieldName("field")));
                }
                return new MemberAccess((Expression) fromTSNode(node.getChildByFieldName("argument")), (SimpleIdentifier) fromTSNode(node.getChildByFieldName("field")));
            }
        } else {
            throw new MeaningTreeException("Unknown identifier: " + node.getType());
        }
    }

    @NotNull
    private Expression fromAssignmentExpression(@NotNull TSNode node) {
        Expression left = (Expression) fromTSNode(node.getChildByFieldName("left"));
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

        if (left instanceof PointerUnpackOp ptrOp) {
            if (ptrOp.getArgument() instanceof ParenthesizedExpression p && p.getExpression() instanceof AddOp binOp) {
                Expression leftmost = binOp.getLeft();
                List<Expression> args = new ArrayList<>();
                args.add(binOp.getRight());
                while (leftmost instanceof AddOp op) {
                    leftmost = op.getLeft();
                    args.add(op.getRight());
                }
                left = new IndexExpression(leftmost, BinaryExpression.
                        fromManyOperands(args.reversed().toArray(new Expression[0]), 0, AddOp.class), true);
            } else if (getConfigParameter("expressionMode").getBooleanValue()) {
                return right;
            }
        }

        return new AssignmentExpression(left, right, augmentedAssignmentOperator);
    }

    @NotNull
    private NumericLiteral fromNumberLiteral(@NotNull TSNode node) {
        String value = getCodePiece(node);
        if (value.contains(".")) {
            return new FloatLiteral(value);
        }
        return new IntegerLiteral(value);
    }

    @NotNull
    private Node fromTranslationUnit(@NotNull TSNode node) {
        List<Node> nodes = new ArrayList<>();
        FunctionDefinition entryPoint = null;
        for (int i = 0; i < node.getNamedChildCount(); i++) {
            TSNode currNode = node.getNamedChild(i);
            if (currNode.getType().equals("ERROR")) {
                Node errorNode = fromTSNode(currNode);
                if (errorNode instanceof Expression expr) {
                    nodes.add(new ExpressionStatement(expr));
                } else {
                    nodes.add(errorNode);
                }
            } else {
                Node converted = fromTSNode(currNode);
                if (converted instanceof ExpressionStatement exprStmt && exprStmt.getExpression() == null) {
                    continue;
                }
                if (converted instanceof FunctionDefinition funcDecl && funcDecl.getName().equalsIdentifier("main")) {
                    entryPoint = funcDecl;
                }
                nodes.add(converted);
            }
        }
        SymbolEnvironment context = new SymbolEnvironment(null); //TODO: fix symbol table
        return new ProgramEntryPoint(context, nodes, entryPoint);
    }

    @NotNull
    private Node fromExpressionStatement(@NotNull TSNode node) {
        if (node.getNamedChild(0).isNull()) {
            return new ExpressionStatement(null);
        }
        Expression expr = (Expression) fromTSNode(node.getNamedChild(0));
        if (expr instanceof AssignmentExpression assignmentExpression) {
            return assignmentExpression.toStatement();
        }
        return new ExpressionStatement(expr);
    }
}
