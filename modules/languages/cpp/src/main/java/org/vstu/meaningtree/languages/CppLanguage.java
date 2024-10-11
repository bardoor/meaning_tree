package org.vstu.meaningtree.languages;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.treesitter.*;
import org.vstu.meaningtree.MeaningTree;
import org.vstu.meaningtree.exceptions.MeaningTreeException;
import org.vstu.meaningtree.nodes.*;
import org.vstu.meaningtree.nodes.declarations.SeparatedVariableDeclaration;
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
import org.vstu.meaningtree.nodes.expressions.identifiers.QualifiedIdentifier;
import org.vstu.meaningtree.nodes.expressions.identifiers.ScopedIdentifier;
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
import org.vstu.meaningtree.nodes.statements.ExpressionSequence;
import org.vstu.meaningtree.nodes.statements.ExpressionStatement;
import org.vstu.meaningtree.nodes.types.NoReturn;
import org.vstu.meaningtree.nodes.types.UnknownType;
import org.vstu.meaningtree.nodes.types.UserType;
import org.vstu.meaningtree.nodes.types.builtin.*;
import org.vstu.meaningtree.nodes.types.containers.DictionaryType;
import org.vstu.meaningtree.nodes.types.containers.ListType;
import org.vstu.meaningtree.nodes.types.containers.SetType;
import org.vstu.meaningtree.nodes.types.containers.components.Shape;
import org.vstu.meaningtree.nodes.types.user.Class;
import org.vstu.meaningtree.nodes.types.user.GenericClass;
import org.vstu.meaningtree.utils.env.SymbolEnvironment;

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

    @Override
    public TSTree getTSTree() {
        TSTree tree = _parser.parseString(null, _code);
        try {
            tree.printDotGraphs(new File("TSTree.dot"));
        } catch (IOException e) { }
        return tree;
    }

    @NotNull
    public synchronized MeaningTree getMeaningTree(String code) {
        _code = code;
        TSNode rootNode = getRootNode();
        List<String> errors = lookupErrors(rootNode);
        if (!errors.isEmpty()) {
            throw new MeaningTreeException(String.format("Given code has syntax errors: %s", errors));
        }
        return new MeaningTree(fromTSNode(rootNode));
    }

    @Override
    public LanguageTokenizer getTokenizer() {
        return new CppTokenizer(_code, this);
    }

    @NotNull
    private Node fromTSNode(@NotNull TSNode node) {
        Objects.requireNonNull(node);

        return switch (node.getType()) {
            case "translation_unit" -> fromTranslationUnit(node);
            case "expression_statement"-> fromExpressionStatement(node);
            case "parameter_pack_expansion" -> fromTSNode(node.getNamedChild(0));
            case "binary_expression" -> fromBinaryExpression(node);
            case "unary_expression" -> fromUnaryExpression(node);
            case "parenthesized_expression" -> fromParenthesizedExpression(node);
            case "update_expression" -> fromUpdateExpression(node);
            case "call_expression" -> fromCallExpression(node);
            case "conditional_expression" -> fromConditionalExpression(node);
            case "comma_expression" -> fromCommaExpression(node);
            case "subscript_expression" -> fromSubscriptExpression(node);
            case "assignment_expression" -> fromAssignmentExpression(node);
            case "declaration" -> fromDeclaration(node);
            case "identifier", "qualified_identifier", "field_expression", "namespace_identifier", "type_identifier", "field_identifier" -> fromIdentifier(node);
            case "number_literal" -> fromNumberLiteral(node);
            case "string_literal" -> fromStringLiteral(node);
            case "user_defined_literal" -> fromUserDefinedLiteral(node);
            case "null" -> new NullLiteral();
            case "true" -> new BoolLiteral(true);
            case "false" -> new BoolLiteral(false);
            case "initializer_list" -> fromInitializerList(node);
            case "primitive_type", "placeholder_type_specifier", "sized_type_specifier", "type_descriptor" -> fromType(node);
            case "sizeof_expression" -> fromSizeOf(node);
            case "new_expression" -> fromNewExpression(node);
            case "delete_expression" -> fromDeleteExpression(node);
            case "cast_expression" -> fromCastExpression(node);
            case "pointer_expression" -> fromPointerExpression(node);
            default -> throw new UnsupportedOperationException(String.format("Can't parse %s this code:\n%s", node.getType(), getCodePiece(node)));
        };
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
                dimensions.add((Expression) fromTSNode(declarator.getNamedChild(1).getNamedChild(0)));
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
            case "size_t" -> new IntType(32, true);
            case "int16_t" -> new IntType(16);
            case "int32_t", "time32_t" -> new IntType(32);
            case "int64_t", "time64_t" -> new IntType(64);
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
        if (type.matches(".*(long|int|short|unsigned).*")) {
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
            if (subType.equals("int")) {
                return new IntType(size, isUnsigned);
            } else {
                return new FloatType(size);
            }
        } else {
            throw new UnsupportedOperationException(String.format("Can't parse sized type %s this code:\n%s", node.getType(), getCodePiece(node)));
        }
    }

    private Node fromStringLiteral(TSNode node) {
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
    private Node fromCallExpression(@NotNull TSNode node) {
        Identifier functionName = (Identifier) fromTSNode(node.getChildByFieldName("function"));

        TSNode tsArguments = node.getChildByFieldName("arguments");
        List<Expression> arguments = new ArrayList<>();
        for (int i = 0; i < tsArguments.getNamedChildCount(); i++) {
            TSNode tsArgument = tsArguments.getNamedChild(i);
            Expression argument = (Expression) fromTSNode(tsArgument);
            arguments.add(argument);
        }

        if (functionName.toString().equals("pow") && arguments.size() == 2) {
            return new PowOp(arguments.getFirst(), arguments.getLast());
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
            case "&&", "and" -> new ShortCircuitAndOp(left, right);
            case "||", "or" -> new ShortCircuitOrOp(left, right);
            case "&" -> new BitwiseAndOp(left, right);
            case "|" -> new BitwiseOrOp(left, right);
            case "^" -> new XorOp(left, right);
            case "<<" -> new LeftShiftOp(left, right);
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
                }
                TSNode tsValue = tsDeclarator.getChildByFieldName("value");

                SimpleIdentifier variableName = (SimpleIdentifier) fromTSNode(tsVariableName);
                Expression value = (Expression) fromTSNode(tsValue);

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
                return new ScopedIdentifier(ident, (SimpleIdentifier) fromIdentifier(node.getChildByFieldName("field")));
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
                return new ScopedIdentifier(identList);
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
        return new IntegerLiteral(value);
    }

    @NotNull
    private Node fromTranslationUnit(@NotNull TSNode node) {
        // TODO: Temporary solution
        if (node.getNamedChildCount() == 1) {
            return fromTSNode(node.getNamedChild(0));
        } else {
            List<Node> nodes = new ArrayList<>();
            for (int i = 0; i < node.getNamedChildCount(); i++) {
                nodes.add(fromTSNode(node.getNamedChild(i)));
            }
            SymbolEnvironment context = new SymbolEnvironment(null);
            return new ProgramEntryPoint(context, nodes);
        }
    }

    @NotNull
    private ExpressionStatement fromExpressionStatement(@NotNull TSNode node) {
        Expression expr = (Expression) fromTSNode(node.getChild(0));
        return new ExpressionStatement(expr);
    }
}
