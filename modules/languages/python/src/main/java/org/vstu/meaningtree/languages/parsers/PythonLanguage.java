package org.vstu.meaningtree.languages.parsers;

import org.treesitter.TreeSitterPython;
import org.treesitter.TSLanguage;
import org.treesitter.TSNode;
import org.treesitter.TSTree;
import org.treesitter.TSParser;

import org.vstu.meaningtree.MeaningTree;
import org.vstu.meaningtree.languages.utils.PseudoCompoundStatement;
import org.vstu.meaningtree.nodes.*;
import org.vstu.meaningtree.nodes.bitwise.*;
import org.vstu.meaningtree.nodes.comparison.*;
import org.vstu.meaningtree.nodes.declarations.*;
import org.vstu.meaningtree.nodes.definitions.ClassDefinition;
import org.vstu.meaningtree.nodes.definitions.DefinitionArgument;
import org.vstu.meaningtree.nodes.definitions.FunctionDefinition;
import org.vstu.meaningtree.nodes.identifiers.Identifier;
import org.vstu.meaningtree.nodes.identifiers.ScopedIdentifier;
import org.vstu.meaningtree.nodes.identifiers.SimpleIdentifier;
import org.vstu.meaningtree.nodes.literals.*;
import org.vstu.meaningtree.nodes.logical.NotOp;
import org.vstu.meaningtree.nodes.logical.ShortCircuitAndOp;
import org.vstu.meaningtree.nodes.logical.ShortCircuitOrOp;
import org.vstu.meaningtree.nodes.math.*;
import org.vstu.meaningtree.nodes.modules.Alias;
import org.vstu.meaningtree.nodes.modules.Import;
import org.vstu.meaningtree.nodes.modules.ImportAll;
import org.vstu.meaningtree.nodes.modules.ImportMembers;
import org.vstu.meaningtree.nodes.statements.*;
import org.vstu.meaningtree.nodes.types.*;
import org.vstu.meaningtree.nodes.unary.UnaryMinusOp;
import org.vstu.meaningtree.nodes.unary.UnaryPlusOp;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Stream;

public class PythonLanguage extends Language {

    @Override
    public MeaningTree getMeaningTree(String code) {
        _code = code;
        TSParser parser = new TSParser();
        TSLanguage pyLanguage = new TreeSitterPython();
        parser.setLanguage(pyLanguage);

        TSTree tree = parser.parseString(null, code);

        if (tree.getRootNode().hasError()) {
            throw new RuntimeException("Code contains syntax errors\n" + code);
        }

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
            case "module" -> createEntryPoint(node);
            case "block" -> fromCompoundTSNode(node);
            case "if_statement" -> fromIfStatementTSNode(node);
            case "expression_statement", "expression_list", "tuple_pattern" -> fromExpressionSequencesTSNode(node);
            case "parenthesized_expression" -> fromParenthesizedExpressionTSNode(node);
            case "binary_operator" -> fromBinaryExpressionTSNode(node);
            case "unary_operator" -> fromUnaryExpressionTSNode(node);
            case "not_operator" -> fromNotOperatorTSNode(node);
            case "pass_statement", "ellipsis" -> null;
            case "integer" -> fromIntegerLiteralTSNode(node);
            case "float" -> fromFloatLiteralTSNode(node);
            case "identifier" -> fromIdentifier(node);
            case "keyword_argument" -> fromDefinitionArgument(node);
            case "delete_statement" -> new DeleteStatement((Expression) fromTSNode(node.getChild(0)));
            case "comparison_operator" -> fromComparisonTSNode(node);
            case "list", "set", "tuple" -> fromList(node, node.getType());
            case "dictionary" -> fromDictionary(node);
            case "string" -> fromString(node);
            case "interpolation" -> fromTSNode(node.getNamedChild(0));
            case "slice" -> fromSlice(node);
            case "for_statement" -> fromForLoop(node);
            case "class_definition" -> fromClass(node);
            case "comment" -> fromComment(node);
            case "boolean_operator" -> fromBooleanOperatorTSNode(node);
            case "none" -> new NullLiteral();
            case "type" -> determineType(node);
            case "true" -> new BoolLiteral(true);
            case "false" -> new BoolLiteral(false);
            case "call" -> fromFunctionCall(node);
            case "break_statement" -> new BreakStatement();
            case "continue_statement" -> new ContinueStatement();
            case "subscript" -> fromIndexTSNode(node);
            case "dotted_name" -> fromDottedNameTSNode(node);
            case "aliased_import" -> new Alias((Identifier)fromTSNode(node.getChildByFieldName("name")), (SimpleIdentifier) fromTSNode(node.getChildByFieldName("alias")));
            case "import_statement", "import_from_statement" -> fromImportNodes(node);
            case "attribute"-> fromAttributeTSNode(node);
            case "return_statement" -> fromReturnTSNode(node);
            case "conditional_expression"-> fromTernaryOperatorTSNode(node);
            case "named_expression" -> fromAssignmentExpressionTSNode(node);
            case "assignment", "augmented_assignment" -> fromAssignmentStatementTSNode(node);
            case "function_definition" -> fromFunctionTSNode(node);
            case "decorated_definition" -> detectAnnotated(node);
            case "while_statement" -> fromWhileLoop(node);
            case "match_statement" -> fromMatchStatement(node);
            case null, default -> throw new UnsupportedOperationException(String.format("Can't parse %s", node.getType()));
        };
    }

    private Node fromMatchStatement(TSNode node) {
        Expression target = (Expression) fromTSNode(node.getChildByFieldName("subject"));
        node = node.getChildByFieldName("body");
        List<ConditionBranch> branches = new ArrayList<>();
        Statement defaultBranch = null;
        for (int i = 0; i < node.getNamedChildCount(); i++) {
            TSNode alternative = node.getNamedChild(i);
            Expression condition;
            VariableDeclaration newDecl = null;
            if (alternative.getNamedChild(0).getNamedChildCount() == 0) {
                defaultBranch = (Statement) fromTSNode(alternative.getChildByFieldName("consequence"));
                continue;
            } else if (alternative.getNamedChild(0).getNamedChild(0).getType().equals("as_pattern")) {
                condition = (Expression) fromTSNode(alternative.getNamedChild(0).getNamedChild(0).getNamedChild(0).getNamedChild(0));
                SimpleIdentifier ident = (SimpleIdentifier) fromTSNode(alternative.getNamedChild(0).getNamedChild(0).getNamedChild(1));
                newDecl = new VariableDeclaration(new UnknownType(), ident, condition);
            } else {
                condition = (Expression) fromTSNode(alternative.getNamedChild(0).getNamedChild(0));
            }
            CompoundStatement compoundStatement = (CompoundStatement) fromTSNode(alternative.getChildByFieldName("consequence"));
            if (newDecl != null) {
                compoundStatement.insert(0, newDecl);
            }
            branches.add(new ConditionBranch(condition, compoundStatement));
        }
        return new SwitchStatement(target, branches, defaultBranch);
    }

    private Node detectAnnotated(TSNode node) {
        TSNode definition = node.getChildByFieldName("definition");
        if (definition.getType().equals("class_definition")) {
            return fromClass(definition);
        } else {
            return fromFunctionTSNode(node);
        }
    }

    private DefinitionArgument fromDefinitionArgument(TSNode node) {
        SimpleIdentifier ident = (SimpleIdentifier) fromTSNode(node.getChildByFieldName("name"));
        Expression expr = (Expression) fromTSNode(node.getChildByFieldName("value"));
        return new DefinitionArgument(ident, expr);
    }

    private Node fromImportNodes(TSNode node) {
        if (node.getType().equals("import_statement")) {
            List<Identifier> scopes = new ArrayList<>();
            List<Import> imports = new ArrayList<>();
            TSNode currentChild = node.getChildByFieldName("name");
            while (currentChild != null && !currentChild.isNull()) {
                scopes.add((Identifier) fromTSNode(currentChild));
                currentChild = currentChild.getNextNamedSibling();
            }
            for (Identifier scope : scopes) {
                imports.add(new ImportMembers(scope));
            }
            if (imports.size() == 1) {
                return imports.getFirst();
            } else {
                return new PseudoCompoundStatement(imports.toArray(new Node[0]));
            }
        } else if (node.getType().equals("import_from_statement")) {
            Identifier scope = (Identifier) fromTSNode(node.getChildByFieldName("module_name"));
            if (node.getNamedChild(1).getType().equals("wildcard_import")) {
                return new ImportAll(scope);
            }
            List<Identifier> members = new ArrayList<>();
            TSNode currentChild = node.getChildByFieldName("name");
            while (currentChild != null && !currentChild.isNull()) {
                members.add((Identifier) fromTSNode(currentChild));
                currentChild = currentChild.getNextNamedSibling();
            }
            return new ImportMembers(scope, members);
        } else {
            return fromTSNode(node);
        }
    }

    private FunctionCall fromFunctionCall(TSNode node) {
        Node ident = fromTSNode(node.getChildByFieldName("function"));
        if (ident instanceof MemberAccess memAccess) {
            ident = memAccess.toScopedIdentifier();
        }
        List<Expression> exprs = new ArrayList<>();
        TSNode arguments = node.getChildByFieldName("arguments");
        for (int i = 0; i < arguments.getNamedChildCount(); i++) {
            String tsNodeChildType = arguments.getNamedChild(i).getType();
            if (tsNodeChildType.equals("(") || tsNodeChildType.equals(")") || tsNodeChildType.equals(",")) {
                continue;
            }
            Expression expr = (Expression) fromTSNode(arguments.getNamedChild(i));
            exprs.add(expr);
        }
        return new FunctionCall((Identifier) ident, exprs);
    }

    private Annotation fromDecorator(TSNode node) {
        TSNode child = node.getNamedChild(0);
        if (child.getType().equals("call")) {
            FunctionCall call = fromFunctionCall(node);
            return new Annotation(call.getFunctionName(), call.getArguments().toArray(new Expression[0]));
        } else {
            Node ident = fromTSNode(node.getNamedChild(0));
            if (ident instanceof MemberAccess memAccess) {
                ident = memAccess.toScopedIdentifier();
            }
            return new Annotation((Identifier) ident);
        }
    }

    private Node fromFunctionTSNode(TSNode node) {
        List<Annotation> anno = new ArrayList<>();
        if (node.getType().equals("decorated_definition")) {
            TSNode decorator = node.getNamedChild(0);
            anno.add(fromDecorator(decorator));
            while (decorator.getNextNamedSibling().getType().equals("decorator")) {
                decorator = decorator.getNextNamedSibling();
                anno.add(fromDecorator(decorator));
            }
            node = node.getChildByFieldName("definition");
        }
        SimpleIdentifier name = new SimpleIdentifier(getCodePiece(node.getChildByFieldName("name")));
        List<DeclarationArgument> arguments = new ArrayList<>();
        for (int i = 0; i < node.getChildByFieldName("parameters").getNamedChildCount(); i++) {
            arguments.add(fromDeclarationArgument(node.getChildByFieldName("parameters").getNamedChild(i)));
        }
        Type returnType = determineType(node.getChildByFieldName("return_type"));
        Statement body = (Statement) fromTSNode(node.getChildByFieldName("body"));
        return new FunctionDefinition(new FunctionDeclaration(name, returnType, anno, arguments.toArray(new DeclarationArgument[0])), body);
    }

    private DeclarationArgument fromDeclarationArgument(TSNode namedChild) {
        Type type = null;
        Expression initial = null;
        boolean isListUnpacking = false;
        if (namedChild.getType().equals("typed_parameter")) {
            type = determineType(namedChild.getChildByFieldName("type"));
        }  else if (namedChild.getType().equals("typed_default_parameter")) {
            type = determineType(namedChild.getChildByFieldName("type"));
            initial = (Expression) fromTSNode(namedChild.getChildByFieldName("value"));
        } else if (namedChild.getType().equals("default_parameter")) {
            initial = (Expression) fromTSNode(namedChild.getChildByFieldName("value"));
        } else if (namedChild.getType().equals("list_splat_pattern")) {
            isListUnpacking = true;
        }
        SimpleIdentifier identifier = (SimpleIdentifier) fromTSNode(namedChild.getNamedChild(0));
        return new DeclarationArgument(type, isListUnpacking, identifier, initial);
    }

    private ClassDefinition fromClass(TSNode node) {
        ClassDeclaration classDecl = new ClassDeclaration((SimpleIdentifier) fromTSNode(node.getChildByFieldName("name")));
        UserType type = new UserType((SimpleIdentifier) classDecl.getName());
        CompoundStatement body = (CompoundStatement) fromTSNode(node.getChildByFieldName("body"));
        List<Node> nodes = new ArrayList<>();
        for (Node bodyNode : body) {
            if (bodyNode instanceof VariableDeclaration var) {
                nodes.add(var.makeField(List.of(Modifier.PUBLIC)));
            } else if (bodyNode instanceof FunctionDefinition func) {
                boolean isStatic = false;
                List<Annotation> anno = ((FunctionDeclaration) (func.getDeclaration())).getAnnotations();
                for (Annotation annotation : anno) {
                    isStatic = annotation.getName().toString().equals("staticmethod") || annotation.getName().toString().equals("classmethod");
                    if (isStatic) break;
                }
                List<Modifier> modifiers = new ArrayList<>();
                if (isStatic) {
                    modifiers.add(Modifier.STATIC);
                }
                modifiers.add(Modifier.PUBLIC);
                nodes.add(func.makeMethod(type, modifiers));
            } else {
                nodes.add(bodyNode);
            }
        }
        return new ClassDefinition(classDecl, nodes);
    }

    private Statement fromForLoop(TSNode node) {
        SimpleIdentifier left = (SimpleIdentifier) fromTSNode(node.getChildByFieldName("left"));
        Node right = fromTSNode(node.getChildByFieldName("right"));
        Statement body = (Statement) fromTSNode(node.getChildByFieldName("body"));
        if (right instanceof FunctionCall call) {
            String funcName = call.getFunctionName().toString();
            if (funcName.equals("range")) {
                List<Expression> args = call.getArguments();
                Expression start = null, stop = null, step = null;
                switch (args.size()) {
                    case 0:
                        throw new IllegalArgumentException("Range requires at least 1 argument");
                    case 1:
                        stop = args.getFirst();
                        start = new IntegerLiteral("0");
                        step = new IntegerLiteral("1");
                        break;
                    case 2:
                        start = args.getFirst();
                        stop = args.get(1);
                        step = new IntegerLiteral("1");
                        break;
                    default:
                        start = args.getFirst();
                        stop = args.get(1);
                        step = args.get(2);
                        break;
                }
                return new RangeForLoop(start, stop, step, left, body);
            }
            return new ForEachLoop(new VariableDeclaration(null, left), (Expression) right, body);
        } else {
            return new ForEachLoop(new VariableDeclaration(null, left), (Expression) right, body);
        }
    }

    private WhileLoop fromWhileLoop(TSNode node) {
        Expression condition = (Expression) fromTSNode(node.getChildByFieldName("condition"));
        Statement body = (Statement)  fromTSNode(node.getChildByFieldName("body"));
        return new WhileLoop(condition, body);
    }

    private IndexExpression fromIndexTSNode(TSNode node) {
        Expression base = (Expression) fromTSNode(node.getChildByFieldName("value"));
        TSNode subscriptNode = node.getChildByFieldName("subscript");
        ArrayList<Expression> subscripts = new ArrayList<>();
        subscripts.add((Expression) fromTSNode(subscriptNode));
        while (!subscriptNode.getNextNamedSibling().isNull()) {
            subscriptNode = subscriptNode.getNextNamedSibling();
            subscripts.add((Expression) fromTSNode(subscriptNode));
        }
        if (subscripts.size() > 1) {
            return new IndexExpression(base, new ExpressionSequence(subscripts));
        } else {
            return new IndexExpression(base, subscripts.getFirst());
        }
    }

    private MemberAccess fromAttributeTSNode(TSNode node) {
        Expression expr = (Expression) fromTSNode(node.getChildByFieldName("object"));
        SimpleIdentifier member = (SimpleIdentifier) fromTSNode(node.getChildByFieldName("attribute"));
        return new MemberAccess(expr, member);
    }

    private DictionaryLiteral fromDictionary(TSNode node) {
        LinkedHashMap<Expression, Expression> dict = new LinkedHashMap<>();
        for (int i = 0; i < node.getNamedChildCount(); i++) {
            Expression key = (Expression) fromTSNode(node.getNamedChild(i).getChildByFieldName("key"));
            Expression value = (Expression) fromTSNode(node.getNamedChild(i).getChildByFieldName("value"));
            dict.put(key, value);
        }
        return new DictionaryLiteral(dict);
    }

    private TernaryOperator fromTernaryOperatorTSNode(TSNode node) {
        Expression thenExpr = (Expression) fromTSNode(node.getNamedChild(0));
        Expression ifCond = (Expression) fromTSNode(node.getNamedChild(1));
        Expression elseExpr = (Expression) fromTSNode(node.getNamedChild(2));
        return new TernaryOperator(ifCond, thenExpr, elseExpr);
    }

    private Identifier fromDottedNameTSNode(TSNode node) {
        List<SimpleIdentifier> members = new ArrayList<>();
        for (int i = 0; i < node.getNamedChildCount(); i++) {
            members.add((SimpleIdentifier) fromTSNode(node.getNamedChild(i)));
        }
        if (members.size() == 1) {
            return members.getFirst();
        }
        return new ScopedIdentifier(members.toArray(new SimpleIdentifier[0]));
    }

    private Range fromSlice(TSNode node) {
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

    private ReturnStatement fromReturnTSNode(TSNode node) {
        if (node.getNamedChildCount() > 0) {
            return new ReturnStatement((Expression) fromTSNode(node.getNamedChild(0)));
        }
        return new ReturnStatement(null);
    }

    private Node createEntryPoint(TSNode node) {
        // detect if __name__ == __main__ construction
        CompoundStatement compound = fromCompoundTSNode(node);
        Node entryPointNode = null;
        IfStatement entryPointIf = null;
        for (Node programNode : compound) {
            if (programNode instanceof IfStatement ifStmt) {
                ConditionBranch mainBranch = ifStmt.getBranches().get(0);
                if (mainBranch.getCondition() instanceof EqOp eqOp) {
                    if (eqOp.getLeft().toString().equals("__name__") && eqOp.getRight().toString().equals("__main__")) {
                        entryPointNode = mainBranch.getBody();
                        entryPointIf = ifStmt;
                    }
                }
            }
        }
        List<Node> nodes = new ArrayList<>(List.of(compound.getNodes()));
        nodes.remove(entryPointIf);
        return new ProgramEntryPoint(nodes, entryPointNode);
    }

    private Identifier fromIdentifier(TSNode node) {
        return new SimpleIdentifier(getCodePiece(node));
    }

    private Node fromString(TSNode node) {
        TSNode content = node.getChild(1);
        if (getCodePiece(node.getChild(0)).equals("\"\"\"")
                && node.getParent().getType().equals("expression_statement")
                && node.getParent().getNamedChildCount() == 1) {
            return Comment.fromUnescaped(getCodePiece(content));
        }
        StringLiteral.Type type = StringLiteral.Type.NONE;

        if (Stream.of("fr", "r", "rf")
                .anyMatch((String prefix) -> getCodePiece(node.getChild(0)).startsWith(prefix))) {
            type = StringLiteral.Type.RAW;
        }

        if (Stream.of("fr", "f", "rf")
                .anyMatch((String prefix) -> getCodePiece(node.getChild(0)).startsWith(prefix))) {
            TSNode contentNode = node.getNamedChild(1);
            List<Expression> interpolation = new ArrayList<>();

            while (!contentNode.getType().equals("string_end")) {
                if (contentNode.getType().equals("string_content")) {
                    interpolation.add(StringLiteral.fromEscaped(getCodePiece(contentNode), type));
                } else {
                    interpolation.add((Expression) fromTSNode(contentNode));
                }
                contentNode = contentNode.getNextNamedSibling();
            }
            return new InterpolatedStringLiteral(type, interpolation);
        }

        if (type == StringLiteral.Type.RAW) {
            return StringLiteral.fromUnescaped(getCodePiece(content), type);
        } else {
            return StringLiteral.fromEscaped(getCodePiece(content), type);
        }
    }

    private Comment fromComment(TSNode node) {
        return Comment.fromEscaped(getCodePiece(node).replace("#", "").trim());
    }

    private Type determineType(TSNode typeNode) {
        if (typeNode.isNull()) {
            return new UnknownType();
        }
        if (typeNode.getNamedChildCount() > 0 && typeNode.getNamedChild(0).getType().equals("generic_type")) {
            TSNode genericTypeNode = typeNode.getNamedChild(0);
            List<Type> genericTypes = new ArrayList<>();
            String typeName = getCodePiece(typeNode.getNamedChild(0).getNamedChild(0));
            for (int i = 0; i < genericTypeNode.getNamedChild(1).getNamedChildCount(); i++) {
                genericTypes.add(determineType(genericTypeNode.getNamedChild(1).getNamedChild(i)));
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

    private Node fromAssignmentExpressionTSNode(TSNode node) {
        Expression left = (Expression) fromTSNode(node.getChildByFieldName("name"));
        Expression right = (Expression) fromTSNode(node.getChildByFieldName("value"));
        return new AssignmentExpression(left, right);
    }

    private Node fromAssignmentStatementTSNode(TSNode node) {
        TSNode operator = node.getChildByFieldName("left").getNextSibling();
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
                augOp = AugmentedAssignmentOperator.FLOOR_DIV;
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

        if (node.getChildByFieldName("left").getType().equals("pattern_list")) {
            List<Identifier> idents = new ArrayList<>();
            for (int i = 0; i < node.getChildByFieldName("left").getNamedChildCount(); i++) {
                idents.add(fromIdentifier(node.getChildByFieldName("left").getNamedChild(i)));
            }

            List<Expression> exprs = new ArrayList<>();
            if (node.getChildByFieldName("right").getType().equals("expression_list")) {
                for (int i = 0; i < node.getChildByFieldName("right").getNamedChildCount(); i++) {
                    exprs.add((Expression) fromTSNode(node.getChildByFieldName("right").getNamedChild(i)));
                }
            } else {
                exprs.add((Expression) fromTSNode(node.getChildByFieldName("right")));
            }
            while (exprs.size() < idents.size()) {
                exprs.add(new NullLiteral());
            }
            if (idents.size() < exprs.size()) {
                throw new RuntimeException("Invalid using of unpacking construction");
            }

            List<AssignmentStatement> stmts = new ArrayList<>();
            for (int i = 0; i < idents.size(); i++) {
                stmts.add(new AssignmentStatement(idents.get(i), exprs.get(i), augOp));
            }
            return new MultipleAssignmentStatement(stmts);
        }

        Expression left = (Expression) fromTSNode(node.getChildByFieldName("left"));
        Expression right = (Expression) fromTSNode(node.getChildByFieldName("right"));

        if (!node.getChildByFieldName("type").isNull()) {
            Type type = determineType(node.getChildByFieldName("type"));
            return new VariableDeclaration(type, (SimpleIdentifier) left, right);
        }

        return new AssignmentStatement(left, right, augOp);
    }

    private Node fromList(TSNode node, String type) {
        List<Expression> exprs = new ArrayList<>();
        for (int i = 0; i < node.getNamedChildCount(); i++) {
            Expression expr = (Expression) fromTSNode(node.getNamedChild(i));
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
            Node treeNode = fromTSNode(node.getChild(i));
            if (treeNode instanceof PseudoCompoundStatement pcs) {
                for (Node subnode : pcs) {
                    nodes.add(subnode);
                }
            } else if (treeNode != null) {
                nodes.add(treeNode);
            }
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
                elseBranch = (Statement) fromTSNode(altNode.getChildByFieldName("body"));
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

    private UnaryExpression fromUnaryExpressionTSNode(TSNode node) {
        Expression argument = (Expression) fromTSNode(node.getChildByFieldName("argument"));
        TSNode operation = node.getChildByFieldName("operator");
        return switch (getCodePiece(operation)) {
            case "~" -> new InversionOp(argument);
            case "-" -> new UnaryMinusOp(argument);
            case "+" -> new UnaryPlusOp(argument);
            case null, default -> throw new UnsupportedOperationException();
        };
    }

    private NotOp fromNotOperatorTSNode(TSNode node) {
        Expression argument = (Expression) fromTSNode(node.getChildByFieldName("argument"));
        return new NotOp(argument);
    }


    private Node fromExpressionSequencesTSNode(TSNode node) {
        if (node.getNamedChildCount() == 1 && node.getType().equals("expression_statement")) {
            Node n = fromTSNode(node.getChild(0));
            if (n instanceof Statement || n instanceof Declaration || n instanceof Comment) {
                return n;
            }
            return new ExpressionStatement((Expression) n);
        } else {
            Expression[] exprs = new Expression[node.getNamedChildCount()];
            for (int i = 0; i < exprs.length; i++) {
                Node n = fromTSNode(node.getNamedChild(i));
                if (n instanceof Expression expr) {
                    exprs[i] = expr;
                } else {
                    throw new RuntimeException("Invalid type in expression statement, not expression");
                }
            }
            return new ExpressionSequence(exprs);
        }
    }

    private ParenthesizedExpression fromParenthesizedExpressionTSNode(TSNode node) {
        return new ParenthesizedExpression((Expression) fromTSNode(node.getChild(1)));
    }

    private IntegerLiteral fromIntegerLiteralTSNode(TSNode node) {
        String value = getCodePiece(node);
        return new IntegerLiteral(value);
    }

    private FloatLiteral fromFloatLiteralTSNode(TSNode node) {
        String value = getCodePiece(node);
        return new FloatLiteral(value);
    }

    private Node fromComparisonTSNode(TSNode node) {
        Class<? extends BinaryComparison> operator = null;
        ArrayDeque<TSNode> operands = new ArrayDeque<>();

        List<BinaryComparison> comparisons = new ArrayList<>();

        for (int i = 0; i < node.getChildCount(); i++) {
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
        }
        if (comparisons.size() == 1) {
            return comparisons.getFirst();
        }
        return new CompoundComparison(comparisons.toArray(new BinaryComparison[0]));
    }

    private BinaryExpression fromBooleanOperatorTSNode(TSNode node) {
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

    private BinaryExpression fromBinaryExpressionTSNode(TSNode node) {
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