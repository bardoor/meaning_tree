package org.vstu.meaningtree.languages;

import org.apache.commons.text.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;
import org.vstu.meaningtree.exceptions.MeaningTreeException;
import org.vstu.meaningtree.languages.utils.HindleyMilner;
import org.vstu.meaningtree.languages.utils.Scope;
import org.vstu.meaningtree.nodes.*;
import org.vstu.meaningtree.nodes.declarations.ClassDeclaration;
import org.vstu.meaningtree.nodes.declarations.FieldDeclaration;
import org.vstu.meaningtree.nodes.declarations.MethodDeclaration;
import org.vstu.meaningtree.nodes.declarations.VariableDeclaration;
import org.vstu.meaningtree.nodes.declarations.components.DeclarationArgument;
import org.vstu.meaningtree.nodes.declarations.components.VariableDeclarator;
import org.vstu.meaningtree.nodes.definitions.ClassDefinition;
import org.vstu.meaningtree.nodes.definitions.MethodDefinition;
import org.vstu.meaningtree.nodes.definitions.ObjectConstructorDefinition;
import org.vstu.meaningtree.nodes.enums.AugmentedAssignmentOperator;
import org.vstu.meaningtree.nodes.enums.DeclarationModifier;
import org.vstu.meaningtree.nodes.expressions.BinaryExpression;
import org.vstu.meaningtree.nodes.expressions.Identifier;
import org.vstu.meaningtree.nodes.expressions.Literal;
import org.vstu.meaningtree.nodes.expressions.ParenthesizedExpression;
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
import org.vstu.meaningtree.nodes.expressions.other.*;
import org.vstu.meaningtree.nodes.expressions.pointers.PointerPackOp;
import org.vstu.meaningtree.nodes.expressions.pointers.PointerUnpackOp;
import org.vstu.meaningtree.nodes.expressions.unary.*;
import org.vstu.meaningtree.nodes.interfaces.HasInitialization;
import org.vstu.meaningtree.nodes.io.PrintValues;
import org.vstu.meaningtree.nodes.modules.*;
import org.vstu.meaningtree.nodes.statements.CompoundStatement;
import org.vstu.meaningtree.nodes.statements.ExpressionSequence;
import org.vstu.meaningtree.nodes.statements.ExpressionStatement;
import org.vstu.meaningtree.nodes.statements.ReturnStatement;
import org.vstu.meaningtree.nodes.statements.assignments.AssignmentStatement;
import org.vstu.meaningtree.nodes.statements.assignments.MultipleAssignmentStatement;
import org.vstu.meaningtree.nodes.statements.conditions.IfStatement;
import org.vstu.meaningtree.nodes.statements.conditions.SwitchStatement;
import org.vstu.meaningtree.nodes.statements.conditions.components.*;
import org.vstu.meaningtree.nodes.statements.loops.*;
import org.vstu.meaningtree.nodes.statements.loops.control.BreakStatement;
import org.vstu.meaningtree.nodes.statements.loops.control.ContinueStatement;
import org.vstu.meaningtree.nodes.types.GenericUserType;
import org.vstu.meaningtree.nodes.types.NoReturn;
import org.vstu.meaningtree.nodes.types.UnknownType;
import org.vstu.meaningtree.nodes.types.UserType;
import org.vstu.meaningtree.nodes.types.builtin.*;
import org.vstu.meaningtree.nodes.types.containers.ArrayType;
import org.vstu.meaningtree.nodes.types.containers.DictionaryType;
import org.vstu.meaningtree.nodes.types.containers.PlainCollectionType;
import org.vstu.meaningtree.nodes.types.containers.SetType;
import org.vstu.meaningtree.nodes.types.containers.components.Shape;

import java.util.*;
import java.util.stream.Collectors;

import static org.vstu.meaningtree.nodes.enums.AugmentedAssignmentOperator.POW;

public class JavaViewer extends LanguageViewer {

    private final String _indentation;
    private int _indentLevel;
    private final boolean _openBracketOnSameLine;
    private final boolean _bracketsAroundCaseBranches;
    private final boolean _autoVariableDeclaration;

    private Scope _currentScope;
    private Scope _typeScope;

    private void enterNewScope() {
        _currentScope = new Scope(_currentScope);
        _typeScope = new Scope(_typeScope);
    }

    private void leaveScope() {
        Scope parentScope = _currentScope.getParentScope();
        Scope parentTypeScope = _typeScope.getParentScope();
        if (parentScope == null) {
            throw new MeaningTreeException("No parent scope found");
        }
        _currentScope = parentScope;
        _typeScope = parentTypeScope;
    }

    private void addVariableToCurrentScope(@NotNull SimpleIdentifier variableName, Type type) {
        _currentScope.addVariable(variableName, type);
    }

    private void addMethodToCurrentScope(@NotNull SimpleIdentifier methodName, Type returnType) {
        _currentScope.addMethod(methodName, returnType);
    }

    public JavaViewer(int indentSpaceCount,
                      boolean openBracketOnSameLine,
                      boolean bracketsAroundCaseBranches,
                      boolean autoVariableDeclaration
    ) {
        _indentation = " ".repeat(indentSpaceCount);
        _indentLevel = 0;
        _openBracketOnSameLine = openBracketOnSameLine;
        _bracketsAroundCaseBranches = bracketsAroundCaseBranches;
        _currentScope = new Scope();
        _typeScope = new Scope();
        _autoVariableDeclaration = autoVariableDeclaration;
    }

    public JavaViewer() { this(4, true, false, false); }

    @Override
    public String toString(Node node) {
        Objects.requireNonNull(node);

        if (node instanceof Expression expression) {
            HindleyMilner.inference(expression, _typeScope);
        }
        else if (node instanceof Statement statement) {
            HindleyMilner.inference(statement, _typeScope);
        }

        return switch (node) {
            case ListLiteral listLiteral -> toString(listLiteral);
            case SetLiteral setLiteral -> toString(setLiteral);
            case DictionaryLiteral dictionaryLiteral -> toString(dictionaryLiteral);
            case PlainCollectionLiteral unmodifiableListLiteral -> toString(unmodifiableListLiteral);
            case InterpolatedStringLiteral interpolatedStringLiteral -> toString(interpolatedStringLiteral);
            case FloatLiteral l -> toString(l);
            case IntegerLiteral l -> toString(l);
            case StringLiteral l -> toString(l);
            case UserType userType -> toString(userType);
            case ReferenceType ref -> toString(ref.getTargetType());
            case PointerType ptr -> toString(ptr.getTargetType());
            case Type type -> toString(type, true);
            case SelfReference selfReference -> toString(selfReference);
            case UnaryMinusOp unaryMinusOp -> toString(unaryMinusOp);
            case UnaryPlusOp unaryPlusOp -> toString(unaryPlusOp);
            case AddOp op -> toString(op);
            case SubOp op -> toString(op);
            case MulOp op -> toString(op);
            case DivOp op -> toString(op);
            case ModOp op -> toString(op);
            case MatMulOp op -> toString(op);
            case FloorDivOp op -> toString(op);
            case EqOp op -> toString(op);
            case GeOp op -> toString(op);
            case GtOp op -> toString(op);
            case LeOp op -> toString(op);
            case LtOp op -> toString(op);
            case InstanceOfOp op -> toString(op);
            case NotEqOp op -> toString(op);
            case ShortCircuitAndOp op -> toString(op);
            case ShortCircuitOrOp op -> toString(op);
            case NotOp op -> toString(op);
            case ParenthesizedExpression expr -> toString(expr);
            case AssignmentExpression expr -> toString(expr);
            case AssignmentStatement stmt -> toString(stmt);
            case FieldDeclaration decl -> toString(decl);
            case VariableDeclaration stmt -> toString(stmt);
            case CompoundStatement stmt -> toString(stmt);
            case ExpressionStatement stmt -> toString(stmt);
            case SimpleIdentifier expr -> toString(expr);
            case IfStatement stmt -> toString(stmt);
            case GeneralForLoop stmt -> toString(stmt);
            case CompoundComparison cmp -> toString(cmp);
            case RangeForLoop rangeLoop -> toString(rangeLoop);
            case ProgramEntryPoint entryPoint -> toString(entryPoint);
            case MethodCall methodCall -> toString(methodCall);
            case PrintValues printValues -> toString(printValues);
            case FunctionCall funcCall -> toString(funcCall);
            case WhileLoop whileLoop -> toString(whileLoop);
            case ScopedIdentifier scopedIdent -> toString(scopedIdent);
            case PostfixIncrementOp inc -> toString(inc);
            case PostfixDecrementOp dec -> toString(dec);
            case PrefixIncrementOp inc -> toString(inc);
            case PrefixDecrementOp dec -> toString(dec);
            case PowOp op -> toString(op);
            case PackageDeclaration decl -> toString(decl);
            case ClassDeclaration decl -> toString(decl);
            case ClassDefinition def -> toString(def);
            case Comment comment -> toString(comment);
            case BreakStatement stmt -> toString(stmt);
            case ContinueStatement stmt -> toString(stmt);
            case ObjectConstructorDefinition objectConstructor -> toString(objectConstructor);
            case MethodDefinition methodDefinition -> toString(methodDefinition);
            case SwitchStatement switchStatement -> toString(switchStatement);
            case NullLiteral nullLiteral -> toString(nullLiteral);
            case StaticImportAll staticImportAll -> toString(staticImportAll);
            case StaticImportMembers staticImportMembers -> toString(staticImportMembers);
            case ImportAll importAll -> toString(importAll);
            case ImportMembers importMembers -> toString(importMembers);
            case ObjectNewExpression objectNewExpression -> toString(objectNewExpression);
            case BoolLiteral boolLiteral -> toString(boolLiteral);
            case MemberAccess memberAccess -> toString(memberAccess);
            case ArrayNewExpression arrayNewExpression -> toString(arrayNewExpression);
            case ArrayInitializer arrayInitializer -> toString(arrayInitializer);
            case ReturnStatement returnStatement -> toString(returnStatement);
            case CastTypeExpression castTypeExpression -> toString(castTypeExpression);
            case IndexExpression indexExpression -> toString(indexExpression);
            case TernaryOperator ternaryOperator -> toString(ternaryOperator);
            case BitwiseAndOp bitwiseAndOp -> toString(bitwiseAndOp);
            case BitwiseOrOp bitwiseOrOp -> toString(bitwiseOrOp);
            case XorOp xorOp -> toString(xorOp);
            case InversionOp inversionOp -> toString(inversionOp);
            case LeftShiftOp leftShiftOp -> toString(leftShiftOp);
            case RightShiftOp rightShiftOp -> toString(rightShiftOp);
            case MultipleAssignmentStatement multipleAssignmentStatement -> toString(multipleAssignmentStatement);
            case InfiniteLoop infiniteLoop -> toString(infiniteLoop);
            case ExpressionSequence expressionSequence -> toString(expressionSequence);
            case CharacterLiteral characterLiteral -> toString(characterLiteral);
            case DoWhileLoop doWhileLoop -> toString(doWhileLoop);
            case PointerPackOp ptr -> toString(ptr.getArgument());
            case PointerUnpackOp ptr -> toString(ptr.getArgument());
            case ContainsOp op -> toString(op);
            case ReferenceEqOp op -> toString(op);
            default -> throw new IllegalStateException(String.format("Can't stringify node %s", node.getClass()));
        };
    }

    public String toString(ListLiteral list) {
        var builder = new StringBuilder();
        String typeHint = list.getTypeHint() == null ? "" : toString(list.getTypeHint());
        builder.append(String.format("new java.util.ArrayList<%s>() {{", typeHint));
        for (Expression expression : list.getList()) {
            builder.append(String.format("add(%s);", toString(expression)));
        }
        builder.append("}}");
        return builder.toString();
    }

    public String toString(SetLiteral list) {
        var builder = new StringBuilder();
        String typeHint = list.getTypeHint() == null ? "" : toString(list.getTypeHint());
        builder.append(String.format("new java.util.HashSet<%s>() {{", typeHint));
        for (Expression expression : list.getList()) {
            builder.append(String.format("add(%s);", toString(expression)));
        }
        builder.append("}}");
        return builder.toString();
    }

    public String toString(DictionaryLiteral list) {
        var builder = new StringBuilder();
        String keyTypeHint = list.getKeyTypeHint() == null ? "" : toString(list.getKeyTypeHint());
        String valueTypeHint = list.getValueTypeHint() == null || keyTypeHint.isEmpty() ? "" : ", ".concat(toString(list.getValueTypeHint()));
        builder.append(String.format("new java.util.TreeMap<%s%s>() {{", keyTypeHint, valueTypeHint));
        for (Map.Entry<Expression, Expression> entry : list.getDictionary().entrySet()) {
            builder.append(String.format("put(%s, %s);", toString(entry.getKey()), toString(entry.getValue())));
        }
        builder.append("}}");
        return builder.toString();
    }

    public String toString(PlainCollectionLiteral unmodifiableListLiteral) {
        var builder = new StringBuilder();
        String typeHint = unmodifiableListLiteral.getTypeHint() == null ? "Object" : toString(unmodifiableListLiteral.getTypeHint());
        builder.append(String.format("new %s[] {", typeHint));

        for (Expression expression : unmodifiableListLiteral.getList()) {
            builder.append(toString(expression)).append(", ");
        }

        if (builder.length() > 2) {
            builder.deleteCharAt(builder.length() - 1);
            builder.deleteCharAt(builder.length() - 1);
        }

        builder.append("}");
        return builder.toString();
    }

    public String toString(InterpolatedStringLiteral interpolatedStringLiteral) {
        var builder = new StringBuilder();
        var argumentsBuilder = new StringBuilder();

        builder.append("String.format(\"");
        for (Expression stringPart : interpolatedStringLiteral) {
            Type exprType = HindleyMilner.inference(stringPart, _typeScope);
            switch (exprType) {
                case StringType stringType -> {
                    var string = toString(stringPart);
                    builder.append(string, 1, string.length() - 1);
                }
                case IntType integerType -> {
                    builder.append("%d");
                    argumentsBuilder.append(toString(stringPart)).append(", ");
                }
                case FloatType floatType -> {
                    builder.append("%f");
                    argumentsBuilder.append(toString(stringPart)).append(", ");
                }
                default -> {
                    builder.append("%s");
                    argumentsBuilder.append(toString(stringPart)).append(", ");
                }
            }
        }
        builder.append("\"");

        if (argumentsBuilder.length() > 2) {
            argumentsBuilder.deleteCharAt(argumentsBuilder.length() - 1);
            argumentsBuilder.deleteCharAt(argumentsBuilder.length() - 1);

            builder
                    .append(", ")
                    .append(argumentsBuilder.toString());
        }

        builder.append(")");
        return builder.toString();
    }

    public String toString(PrintValues printValues) {
        StringBuilder builder = new StringBuilder();

        builder.append("System.out.");
        builder.append(printValues.addsNewLine() ? "println" : "print");
        builder.append("(");

        if (printValues.valuesCount() > 1) {
            builder.append("String.join(");

            if (printValues.separator != null) {
                builder
                        .append(toString(printValues.separator))
                        .append(", ");
            }

            for (Expression value : printValues.getArguments()) {
                builder
                        .append(toString(value))
                        .append(", ");
            }
            builder.deleteCharAt(builder.length() - 1);
            builder.deleteCharAt(builder.length() - 1);

            if (!printValues.addsNewLine() && printValues.end != null) {
                builder.append(toString(printValues.end));
            }

            builder.append(")");
        }
        else if (printValues.valuesCount() == 1) {
            builder.append(
                    toString(printValues.getArguments().getFirst())
            );
        }

        builder.append(")");

        return builder.toString();
    }

    public String toString(UnaryPlusOp unaryPlusOp) {
        return "+" + toString(unaryPlusOp.getArgument());
    }

    public String toString(UnaryMinusOp unaryMinusOp) {
        return "-" + toString(unaryMinusOp.getArgument());
    }

    private String toString(DoWhileLoop doWhileLoop) {
        StringBuilder builder = new StringBuilder();

        builder.append("do");

        if (_openBracketOnSameLine) {
            builder.append(" {\n");
        }
        else {
            builder.append("\n").append(indent("{\n"));
        }

        List<Node> nodes = new ArrayList<>();
        Statement body = doWhileLoop.getBody();
        if (body instanceof CompoundStatement) {
            nodes.addAll(Arrays.asList(((CompoundStatement) body).getNodes()));
        }
        else {
            nodes.add(body);
        }

        increaseIndentLevel();
        for (Node node : nodes) {
            builder
                    .append(indent(toString(node)))
                    .append("\n");
        }
        decreaseIndentLevel();

        if (_openBracketOnSameLine) {
            builder.append("} ");
        }
        else {
            builder.append("}\n");
        }

        builder.append(
                "while %s;".formatted(
                        toString(doWhileLoop.getCondition())
                )
        );

        return builder.toString();
    }

    private String toString(CharacterLiteral characterLiteral) {
        String symbol = StringEscapeUtils.escapeJava(
                Character.toString(characterLiteral.getValue())
        );
        return "'" + symbol + "'";
    }

    private String toString(ExpressionSequence expressionSequence) {
         StringBuilder builder = new StringBuilder();

         for (Expression expression : expressionSequence.getExpressions()) {
             builder.append(toString(expression)).append(", ");
         }

         // Удаляем лишние пробел и запятую
         if (builder.length() > 2) {
             builder.deleteCharAt(builder.length() - 1);
             builder.deleteCharAt(builder.length() - 1);
         }

         return builder.toString();
    }

    private String toString(InfiniteLoop infiniteLoop) {
        StringBuilder builder = new StringBuilder();

        builder.append(indent("while (true)"));
        Statement body = infiniteLoop.getBody();
        if (body instanceof CompoundStatement compoundStatement) {
            if (_openBracketOnSameLine) {
                builder
                        .append(" ")
                        .append(toString(compoundStatement));
            }
            else {
                builder.append("\n");
                builder.append(indent(toString(body)));
            }
        }
        else {
            builder.append("\n");
            increaseIndentLevel();
            builder.append(indent(toString(body)));
            decreaseIndentLevel();
        }

        return builder.toString();
    }

    private String toString(SelfReference selfReference) {
        return "this";
    }

    private String toString(ObjectConstructorDefinition objectConstructor) {
        MethodDeclaration constructorDeclaration =
                (MethodDeclaration) objectConstructor.getDeclaration();

        StringBuilder builder = new StringBuilder();

        String modifiers = toString(constructorDeclaration.getModifiers());
        if (!modifiers.isEmpty()) {
            builder.append(modifiers).append(" ");
        }

        String name = toString(objectConstructor.getName());
        builder.append(name);

        String parameters = toStringParameters(constructorDeclaration.getArguments());
        if (!parameters.isEmpty()) {
            builder.append(parameters);
        }

        String body = toString(objectConstructor.getBody());
        if (_openBracketOnSameLine)
            { builder.append(" ").append(body); }
        else
            { builder.append("\n").append(indent(body)); }

        return builder.toString();
    }

    private String toString(MultipleAssignmentStatement multipleAssignmentStatement) {
        StringBuilder builder = new StringBuilder();

        for (AssignmentStatement stmt : multipleAssignmentStatement.getStatements()) {
            builder.append(toString(stmt)).append("\n");
        }

        // Удаляем последний перевод строки
        if (builder.length() > 1) {
            builder.deleteCharAt(builder.length() - 1);
        }

        return builder.toString();
    }

    private String toString(RightShiftOp rightShiftOp) {
        return toString(rightShiftOp, ">>");
    }

    private String toString(LeftShiftOp leftShiftOp) {
        return toString(leftShiftOp, "<<");
    }

    private String toString(InversionOp inversionOp) {
        return "~" + toString(inversionOp.getArgument());
    }

    private String toString(XorOp xorOp) {
        return toString(xorOp, "^");
    }

    private String toString(BitwiseOrOp bitwiseOrOp) {
        return toString(bitwiseOrOp, "|");
    }

    private String toString(BitwiseAndOp bitwiseAndOp) {
        return toString(bitwiseAndOp, "&");
    }

    private String toString(TernaryOperator ternaryOperator) {
        String condition = toString(ternaryOperator.getCondition());
        String consequence = toString(ternaryOperator.getThenExpr());
        String alternative = toString(ternaryOperator.getElseExpr());
        return "%s ? %s : %s".formatted(condition, consequence, alternative);
    }

    private String toString(IndexExpression indexExpression) {
        Expression arrayName = indexExpression.getExpr();
        String name = toString(arrayName);
        String index = toString(indexExpression.getIndex());
        return "%s[%s]".formatted(name, index);
    }

    private String toString(CastTypeExpression castTypeExpression) {
        String castType = toString(castTypeExpression.getCastType());
        String value = toString(castTypeExpression.getValue());
        return "(%s) %s".formatted(castType, value);
    }

    private String toString(ReturnStatement returnStatement) {
        Expression expression = returnStatement.getExpression();
        return (expression != null) ? "return %s;".formatted(toString(expression)) : "return;";
    }

    private String toString(ArrayInitializer initializer) {
        StringBuilder builder = new StringBuilder();
        builder.append("{");

        List<Expression> values = initializer.getValues();
        for (Expression value : values) {
            builder
                    .append(toString(value))
                    .append(", ");
        }

        if (builder.length() > 1) {
            // Удаляем лишние пробел и запятую
            builder.deleteCharAt(builder.length() - 1);
            builder.deleteCharAt(builder.length() - 1);
        }

        builder.append("}");
        return builder.toString();
    }

    private String toString(ArrayNewExpression arrayNewExpression) {
        StringBuilder builder = new StringBuilder();
        builder.append("new ");

        String type = toString(arrayNewExpression.getType());
        builder.append(type);

        String dimensions = toString(arrayNewExpression.getShape());
        builder.append(dimensions);

        ArrayInitializer optionalInitializer = arrayNewExpression.getInitializer();
        if (optionalInitializer != null) {
            String initializer = toString(optionalInitializer);
            builder.append(" ").append(initializer);
        }

        return builder.toString();
    }

    private String toString(MemberAccess memberAccess) {
        String object = toString(memberAccess.getExpression());
        String member = toString(memberAccess.getMember());
        return "%s.%s".formatted(object, member);
    }

    private String toString(BoolLiteral boolLiteral) {
        return boolLiteral.getValue() ? "true" : "false";
    }

    private String toString(ObjectNewExpression objectNewExpression) {
        String typeName = toString(objectNewExpression.getType());

        String arguments = objectNewExpression
                .getConstructorArguments()
                .stream()
                .map(this::toString)
                .collect(Collectors.joining(", "));

        return "new %s(%s)".formatted(typeName, arguments);
    }

    private String toString(MethodCall methodCall) {
        String object = toString(methodCall.getObject());
        String methodName = toString(methodCall.getFunctionName());

        String arguments = methodCall
                .getArguments()
                .stream()
                .map(this::toString)
                .collect(Collectors.joining(", "));

        return "%s.%s(%s)".formatted(object, methodName, arguments);
    }

    private String toString(UserType userType) {
        if (userType instanceof GenericUserType generic) {
            String args = Arrays.stream(generic.getTypeParameters()).map(this::toString).collect(Collectors.joining(", "));
            return String.format("%s<%s>", toString(generic.getName()), args);
        }
        return toString(userType.getName());
    }

    private String toString(StaticImportAll staticImportAll) {
        String importTemplate = "import static %s.*;";
        return importTemplate.formatted(toString(staticImportAll.getScope()));
    }

    private String toString(StaticImportMembers staticImportMembers) {
        StringBuilder builder = new StringBuilder();

        String importTemplate = "import static %s.%s;";
        for (Identifier member : staticImportMembers.getMembers()) {
            builder
                    .append(
                            importTemplate.formatted(
                                    toString(staticImportMembers.getScope()),
                                    toString(member)
                            )
                    )
                    .append("\n");
            ;
        }

        // Удаляем последний символ перевода строки
        builder.deleteCharAt(builder.length() - 1);

        return builder.toString();
    }

    private String toString(ImportAll importAll) {
        String importTemplate = "import %s.*;";
        return importTemplate.formatted(toString(importAll.getScope()));
    }

    private String toString(ImportMembers importMembers) {
        StringBuilder builder = new StringBuilder();

        String importTemplate = "import %s.%s;";
        for (Identifier member : importMembers.getMembers()) {
            builder
                    .append(
                        importTemplate.formatted(
                            toString(importMembers.getScope()),
                            toString(member)
                        )
                    )
                    .append("\n");
            ;
        }

        // Удаляем последний символ перевода строки
        if (!importMembers.getMembers().isEmpty()) {
            builder.deleteCharAt(builder.length() - 1);
        }

        return builder.toString();
    }

    private String toString(NullLiteral nullLiteral) {
        return "null";
    }

    private String toStringCaseBlock(CaseBlock caseBlock) {
        StringBuilder builder = new StringBuilder();

        Statement caseBlockBody;
        if (caseBlock instanceof MatchValueCaseBlock mvcb) {
            builder.append("case ");
            builder.append(toString(mvcb.getMatchValue()));
            builder.append(":");
            caseBlockBody = mvcb.getBody();
        }
        else if (caseBlock instanceof DefaultCaseBlock dcb) {
            builder.append("default:");
            caseBlockBody = dcb.getBody();
        }
        else {
            throw new IllegalStateException("Unsupported case block type: " + caseBlock.getClass());
        }

        List<Node> nodesList;
        if (caseBlockBody instanceof CompoundStatement compoundStatement) {
            nodesList = Arrays.asList(compoundStatement.getNodes());
        }
        else {
            nodesList = List.of(caseBlockBody);
        }

        // Внутри case веток нельзя объявлять переменные, нужно обернуть их скобками,
        // поэтому проверяем наличие деклараций переменных
        boolean hasDeclarationInside = false;
        for (Node node : nodesList) {
            if (node instanceof VariableDeclaration) {
                hasDeclarationInside = true;
                break;
            }
        }

        if (!nodesList.isEmpty()) {
            if (_bracketsAroundCaseBranches || hasDeclarationInside) {
                if (_openBracketOnSameLine) {
                    builder.append(" {\n");
                }
                else {
                    builder.append("\n").append(indent("{\n"));
                }
            }
            else {
                builder.append("\n");
            }

            increaseIndentLevel();

            for (Node node : nodesList) {
                builder
                        .append(indent(toString(node)))
                        .append("\n");
            }

            if (caseBlock instanceof BasicCaseBlock || caseBlock instanceof DefaultCaseBlock) {
                builder.append(indent("break;"));
            }
            else {
                builder.deleteCharAt(builder.length() - 1);
            }

            decreaseIndentLevel();

            if (_bracketsAroundCaseBranches || hasDeclarationInside) {
                builder
                        .append("\n")
                        .append(indent("}"));
            }
        }

        return builder.toString();
    }

    private String toString(SwitchStatement switchStatement) {
        StringBuilder builder = new StringBuilder();

        builder.append("switch (");
        builder.append(toString(switchStatement.getTargetExpression()));
        builder.append(") ");

        if (_openBracketOnSameLine) {
            builder.append("{\n");
        }
        else {
            builder.append("\n").append(indent("{\n"));
        }

        increaseIndentLevel();
        for (CaseBlock caseBlock : switchStatement.getCases()) {
            builder
                    .append(indent(toStringCaseBlock(caseBlock)))
                    .append("\n");
        }
        decreaseIndentLevel();

        builder.append(indent("}"));
        return builder.toString();
    }

    private String toString(DeclarationArgument parameter) {
        String type = toString(parameter.getType());
        String name = toString(parameter.getName());
        return "%s %s".formatted(type, name);
    }

    // В отличие от всех остальных методов, данный называется так,
    // чтобы избежать конфликтов с другими методами:
    // toStringParameters(List<Modifier> modifiers)
    // и toStringParameters(List<DeclarationArgument> parameters)
    // с точки зрения Java один и тот же тип...
    private String toStringParameters(List<DeclarationArgument> parameters) {
        StringBuilder builder = new StringBuilder();
        builder.append("(");

        int i;
        for (i = 0; i < parameters.size(); i++) {
            DeclarationArgument parameter = parameters.get(i);
            builder.append("%s, ".formatted(toString(parameter)));
        }

        // Удаляем последний пробел и запятую, если был хотя бы один параметр
        if (i > 0) {
            builder.deleteCharAt(builder.length() - 1);
            builder.deleteCharAt(builder.length() - 1);
        }

        builder.append(")");
        return builder.toString();
    }

    private String toString(MethodDeclaration methodDeclaration) {
        StringBuilder builder = new StringBuilder();

        String modifiersList = toString(methodDeclaration.getModifiers());
        if (!modifiersList.isEmpty()) {
            builder.append(modifiersList).append(" ");
        }

        String returnType = toString(methodDeclaration.getReturnType());
        builder.append(returnType).append(" ");

        String name = toString(methodDeclaration.getName());
        builder.append(name);

        String parameters = toStringParameters(methodDeclaration.getArguments());
        builder.append(parameters);

        return builder.toString();
    }

    private String toString(MethodDefinition methodDefinition) {
        StringBuilder builder = new StringBuilder();

        // Преобразование типа нужно, чтобы избежать вызова toString(Node node)
        String methodDeclaration = toString((MethodDeclaration) methodDefinition.getDeclaration());
        builder.append(methodDeclaration);

        String body = toString(methodDefinition.getBody());
        if (_openBracketOnSameLine)
            { builder.append(" ").append(body); }
        else
            { builder.append("\n").append(indent(body)); }

        return builder.toString();
    }

    private String toString(ContinueStatement stmt) {
        return "continue;";
    }

    private String toString(BreakStatement stmt) {
        return "break;";
    }

    private String toString(Comment comment) {
        if (comment.isMultiline()) {
            return "/*" + comment.getUnescapedContent() + "*/";
        }

        return "//%s".formatted(comment.getUnescapedContent());
    }

    private String toString(FieldDeclaration decl) {
        StringBuilder builder = new StringBuilder();

        String modifiers = toString(decl.getModifiers());
        builder.append(modifiers);
        // Добавляем пробел в конце, если есть хотя бы один модификатор
        if (!builder.isEmpty()) {
            builder.append(" ");
        }

        VariableDeclaration variableDeclaration = new VariableDeclaration(decl.getType(), decl.getDeclarators());
        builder.append(toString(variableDeclaration));

        return builder.toString();
    }

    private String toString(List<DeclarationModifier> modifiers) {
        StringBuilder builder = new StringBuilder();

        for (DeclarationModifier modifier : modifiers) {
            builder.append(
                    switch (modifier) {
                        case PUBLIC -> "public";
                        case PRIVATE -> "private";
                        case PROTECTED -> "protected";
                        case ABSTRACT -> "abstract";
                        case CONST -> "final";
                        case STATIC -> "static";
                        default -> throw new IllegalArgumentException();
                    }
            ).append(" ");
        }

        // Удаляем в конце ненужный пробел, если было более одного модификатора
        if (!builder.isEmpty()) {
            builder.deleteCharAt(builder.length() - 1);
        }

        return builder.toString();
    }

    private String toString(ClassDeclaration decl) {
        String modifiers = toString(decl.getModifiers());
        if (!modifiers.isEmpty()) {
            modifiers += " ";
        }

        return modifiers + "class " + toString(decl.getName());
    }

    private String toString(ClassDefinition def) {
        StringBuilder builder = new StringBuilder();

        String declaration = toString(def.getDeclaration());
        builder.append(declaration);

        String body = toString(def.getBody());
        if (_openBracketOnSameLine)
        { builder.append(" ").append(body); }
        else
        { builder.append("\n").append(indent(body)); }

        return builder.toString();
    }

    public String toString(FloatLiteral literal) {
        String s = Double.toString(literal.getDoubleValue());
        if (!literal.isDoublePrecision()) {
            s = s.concat("f");
        }
        return s;
    }

    public String toString(IntegerLiteral literal) {
        String s = literal.getStringValue(false);
        if (literal.isLong()) {
            s = s.concat("L");
        }
        return s;
    }

    public String toString(StringLiteral literal) {
        if (literal.isMultiline()) {
            return "\"\"\"%s\"\"\"".formatted(literal.getEscapedValue());
        }

        return "\"%s\"".formatted(literal.getEscapedValue());
    }

    private String toString(BinaryExpression expr, String sign) {
        return String.format("%s %s %s", toString(expr.getLeft()), sign, toString(expr.getRight()));
    }

    public String toString(AddOp op) {
        return toString(op, "+");
    }

    public String toString(SubOp op) {
        return toString(op, "-");
    }

    public String toString(MulOp op) {
        return toString(op, "*");
    }

    public String toString(DivOp op) {
        return toString(op, "/");
    }

    public String toString(ModOp op) {
        return toString(op, "%");
    }

    public String toString(FloorDivOp op) {
        return String.format("(long) (%s)", toString(op, "/"));
    }

    public String toString(EqOp op) {
        return toString(op, "==");
    }

    public String toString(GeOp op) {
        return toString(op, ">=");
    }

    public String toString(GtOp op) {
        return toString(op, ">");
    }

    public String toString(LeOp op) {
        return toString(op, "<=");
    }

    public String toString(LtOp op) {
        return toString(op, "<");
    }

    public String toString(InstanceOfOp op) {
        return toString(op, "instanceof");
    }

    public String toString(NotEqOp op) {
        return toString(op, "!=");
    }

    public String toString(ShortCircuitAndOp op) {
        return toString(op, "&&");
    }

    public String toString(ShortCircuitOrOp op) {
        return toString(op, "||");
    }

    public String toString(NotOp op) {
        var arg = op.getArgument();
        if (arg instanceof ParenthesizedExpression || arg instanceof FunctionCall || arg instanceof Literal) {
            return String.format("!%s", toString(arg));
        }
        return String.format("!(%s)", toString(arg));
    }

    public String toString(MatMulOp op) {
        return String.format("matmul(%s, %s)", toString(op.getLeft()), toString(op.getRight()));
    }

    public String toString(ParenthesizedExpression expr) {
        return String.format("(%s)", toString(expr.getExpression()));
    }

    private String toString(AugmentedAssignmentOperator op, Expression left, Expression right) {
        String l = toString(left);
        String r = toString(right);

        // В Java нет встроенного оператора возведения в степень, следовательно,
        // нет и соотвествующего оператора присванивания, поэтому этот случай обрабатываем по особому
        if (op == POW) {
            return "%s = Math.pow(%s, %s)".formatted(l, l, r);
        }

        String o = switch (op) {
            case NONE -> "=";
            case ADD -> "+=";
            case SUB -> "-=";
            case MUL -> "*=";
            // В Java тип деления определяется не видом операции, а типом операндов,
            // поэтому один и тот же оператор
            case DIV, FLOOR_DIV -> "/=";
            case BITWISE_AND -> "&=";
            case BITWISE_OR -> "|=";
            case BITWISE_XOR -> "^=";
            case BITWISE_SHIFT_LEFT -> "<<=";
            case BITWISE_SHIFT_RIGHT -> ">>=";
            case MOD -> "%=";
            default -> throw new IllegalStateException("Unexpected type of augmented assignment operator: " + op);
        };

        if (right instanceof IntegerLiteral integerLiteral
                && (long) integerLiteral.getValue() == 1
                && (o.equals("+=") || o.equals("-="))) {
            o = switch (o) {
                case "+=" -> "++";
                case "-=" -> "--";
                default -> throw new IllegalArgumentException();
            };

            return l + o;
        }

        return "%s %s %s".formatted(l, o, r);
    }

    public String toString(AssignmentExpression expr) {
        return toString(expr.getAugmentedOperator(), expr.getLValue(), expr.getRValue());
    }

    public String toString(AssignmentStatement stmt) {
        AugmentedAssignmentOperator assignmentOperator = stmt.getAugmentedOperator();
        Expression leftValue = stmt.getLValue();
        Expression rightValue = stmt.getRValue();

        if (leftValue instanceof SimpleIdentifier identifier
                && assignmentOperator == AugmentedAssignmentOperator.NONE) {
            Type variableType = _currentScope.getVariableType(identifier);
            // Objects.requireNonNull(variableType);

            if (variableType == null && _autoVariableDeclaration) {
                variableType = _typeScope.getVariableType(identifier);
                Objects.requireNonNull(variableType); // Никогда не будет null...

                String typeName = toString(variableType);
                String variableName = toString(identifier);
                addVariableToCurrentScope(identifier, variableType);
                return "%s %s = %s;".formatted(typeName, variableName, toString(rightValue));
            }
        }

        return "%s;".formatted(toString(assignmentOperator, leftValue, rightValue));
    }

    private String toString(Type type) {
        return toString(type, true);
    }

    private String toString(Type type, boolean isPrimitiveWrapper) {
        return switch (type) {
            case FloatType floatType -> toString(floatType, isPrimitiveWrapper);
            case IntType intType -> toString(intType, isPrimitiveWrapper);
            case BooleanType booleanType -> toString(booleanType, isPrimitiveWrapper);
            case StringType stringType -> toString(stringType);
            case NoReturn voidType -> toString(voidType);
            case UnknownType unknownType -> toString(unknownType);
            case ArrayType arrayType -> toString(arrayType);
            case UserType userType -> toString(userType);
            case CharacterType characterType -> toString(characterType, isPrimitiveWrapper);
            case SetType setType -> toString(setType);
            case DictionaryType dictType -> toString(dictType);
            case PlainCollectionType plain -> toString(plain);
            default -> throw new IllegalStateException("Unexpected value: " + type.getClass());
        };
    }

    public String toString(SetType type) {
        return String.format("java.util.HashSet<%s>", toString(type.getItemType()));
    }

    public String toString(PlainCollectionType type) {
        return String.format("java.util.ArrayList<%s>", toString(type.getItemType()));
    }

    public String toString(DictionaryType type) {
        return String.format("java.util.TreeMap<%s, %s>", toString(type.getKeyType()), toString(type.getValueType()));
    }

    private String toString(FloatType type, boolean isPrimitiveWrapper) {
        if (isPrimitiveWrapper) {
            return type.size == 64 ? "Double" : "Float";
        } else {
            return type.size == 64 ? "double" : "float";
        }
    }

    private String toString(IntType type, boolean isPrimitiveWrapper) {
        if (type.size == 16) {
            return isPrimitiveWrapper ? "Short" : "short";
        } else if (type.size == 32) {
            return isPrimitiveWrapper ? "Integer" : "int";
        } else {
            return isPrimitiveWrapper ? "Long" : "long";
        }
    }

    private String toString(BooleanType type, boolean isPrimitiveWrapper) {
        return isPrimitiveWrapper ? "Boolean" : "boolean";
    }

    private String toString(StringType type) {
        return "String";
    }

    private String toString(NoReturn type) {
        return "void";
    }

    private String toString(UnknownType type) {
        return "Object";
    }

    private String toString(CharacterType type, boolean isPrimitiveWrapper) {
        return isPrimitiveWrapper ? "Character" : "char";
    }

    private String toString(Shape shape) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < shape.getDimensionCount(); i++) {
            builder.append("[");

            Expression dimension = shape.getDimension(i);
            if (dimension != null) {
                builder.append(toString(dimension));
            }

            builder.append("]");
        }

        return builder.toString();
    }

    private String toString(ArrayType type) {
        StringBuilder builder = new StringBuilder();

        String baseType = toString(type.getItemType());
        builder.append(baseType);
        builder.append(toString(type.getShape()));

        return builder.toString();
    }

    private String toString(VariableDeclarator varDecl) {
        StringBuilder builder = new StringBuilder();


        SimpleIdentifier identifier = varDecl.getIdentifier();
        Type variableType = new UnknownType();
        Expression rValue = varDecl.getRValue();
        if (rValue != null) {
            variableType = HindleyMilner.inference(rValue, _typeScope);
        }

        addVariableToCurrentScope(identifier, variableType);

        String identifierName = toString(identifier);
        builder.append(identifierName);

        if (rValue != null) {
            builder.append(" = ").append(toString(rValue));
        }

        return builder.toString();
    }

    public String toString(VariableDeclaration stmt) {
        StringBuilder builder = new StringBuilder();

        Type declarationType = stmt.getType();
        String type = toString(declarationType, false);
        if (declarationType.isConst()) {
            builder.append("final ");
        }
        builder
                .append(type)
                .append(" ");

        for (VariableDeclarator varDecl : stmt.getDeclarators()) {
            builder.append(toString(varDecl)).append(", ");
        }
        // Чтобы избежать лишней головной боли на проверки "а последняя ли это декларация",
        // я автоматически после каждой декларации добавляю запятую и пробел,
        // но для последней декларации они не нужны, поэтому эти два символа удаляются,
        // как сделать красивее - не знаю...
        builder.deleteCharAt(builder.length() - 1);
        builder.deleteCharAt(builder.length() - 1);

        builder.append(";");
        return builder.toString();
    }

    private void increaseIndentLevel() {
        _indentLevel++;
    }

    private void decreaseIndentLevel() {
        _indentLevel--;

        if (_indentLevel < 0) {
            throw new MeaningTreeException("Indentation level can't be less than zero");
        }
    }

    private String indent(String s) {
        if (_indentLevel == 0) {
            return s;
        }

        return _indentation.repeat(Math.max(0, _indentLevel)) + s;
    }

    public String toString(CompoundStatement stmt) {
        StringBuilder builder = new StringBuilder();
        builder.append("{\n");
        increaseIndentLevel();
        for (Node node : stmt) {
            String s = toString(node);
            if (s.isEmpty()) {
                continue;
            }

            s = indent(String.format("%s\n", s));
            builder.append(s);
        }
        decreaseIndentLevel();
        builder.append(indent("}"));
        return builder.toString();
    }

    public String toString(ExpressionStatement stmt) {
        return String.format("%s;", toString(stmt.getExpression()));
    }

    public String toString(SimpleIdentifier identifier) {
        return identifier.getName();
    }

    private String toString(ConditionBranch branch) {
        StringBuilder builder = new StringBuilder();

        String cond = toString(branch.getCondition());
        builder
                .append("(")
                .append(cond)
                .append(")");

        Statement body = branch.getBody();
        if (body instanceof CompoundStatement compStmt) {
            // Если телом ветки является блок кода, то необходимо определить
            // куда нужно добавить фигурные скобки и добавить само тело
            // Пример (для случая, когда скобка на той же строке):
            // if (a > b) {
            //     max = a;
            // }
            if (_openBracketOnSameLine) {
                builder
                        .append(" ")
                        .append(toString(compStmt));
            }
            else {
                builder
                        .append("\n")
                        .append(indent(toString(compStmt)));
            }
        }
        else {
            // В случае если тело ветки не блок кода, то добавляем отступ
            // и вставляем тело
            // Пример:
            // if (a > b)
            //     max = a;
            increaseIndentLevel();
            builder.append("\n").append(indent(toString(body)));
            decreaseIndentLevel();
        }

        return builder.toString();
    }

    private String toString(BinaryComparison binComp) {
        return switch (binComp) {
            case EqOp op -> toString(op);
            case GeOp op -> toString(op);
            case GtOp op -> toString(op);
            case LeOp op -> toString(op);
            case LtOp op -> toString(op);
            case NotEqOp op -> toString(op);
            case ContainsOp op -> toString(op);
            case ReferenceEqOp op -> toString(op);
            default -> throw new IllegalStateException("Unexpected value: " + binComp);
        };
    }

    private String toString(ContainsOp op) {
        String neg = op.isNegative() ? "!" : "";
        String left = toString(op.getRight());
        if (!(op.getRight() instanceof Identifier)) {
            left = "(".concat(left).concat(")");
        }
        return neg.concat(String.format("%s.contains(%s)", left, toString(op.getLeft())));
    }

    private String toString(ReferenceEqOp op) {
        String neg = op.isNegative() ? "!=" : "==";
        return String.format("%s %s %s", toString(op.getLeft()), neg, toString(op.getRight()));
    }

    public String toString(CompoundComparison cmp) {
        StringBuilder builder = new StringBuilder();

        for (BinaryComparison binComp : cmp.getComparisons()) {
            builder.append(toString(binComp)).append(" && ");
        }

        builder.delete(builder.length() - 4, builder.length());

        return builder.toString();
    }

    public String toString(IfStatement stmt) {
        StringBuilder builder = new StringBuilder();

        builder.append("if ");
        List<ConditionBranch> branches = stmt.getBranches();
        builder
                .append(toString(branches.getFirst()))
                .append("\n");

        for (ConditionBranch branch : branches.subList(1, branches.size())) {
            builder
                    .append(indent("else if "))
                    .append(toString(branch))
                    .append("\n");
        }

        if (stmt.hasElseBranch()) {
            builder.append(indent("else"));

            Statement elseBranch = stmt.getElseBranch();
            if (elseBranch instanceof IfStatement innerIfStmt) {
                builder
                        .append(" ")
                        .append(toString(innerIfStmt));
            }
            else if (elseBranch instanceof CompoundStatement innerCompStmt) {
                if (_openBracketOnSameLine) {
                    builder
                            .append(" ")
                            .append(toString(innerCompStmt));
                }
                else {
                    builder
                            .append("\n")
                            .append(indent(toString(innerCompStmt)));
                }
            }
            else {
                builder
                        .append("\n")
                        .append(toString(elseBranch));
            }
        }
        else {
            // Удаляем лишний перевод строки, если ветки else нет
            builder.deleteCharAt(builder.length() - 1);
        }

        return builder.toString();
    }

    private String toString(HasInitialization init) {
        return switch (init) {
            case AssignmentExpression expr -> toString(expr);
            case AssignmentStatement stmt -> toString(stmt);
            case VariableDeclaration decl -> toString(decl);
            case MultipleAssignmentStatement multipleAssignmentStatement -> {
                // Трансляция MultipleAssignmentStatement по умолчанию не подходит -
                // в результате будут получены присваивания, написанные через точку с запятой.
                // Поэтому вручную получаем список присваиваний и создаем правильное отображение.
                StringBuilder builder = new StringBuilder();

                for (AssignmentStatement assignmentStatement : multipleAssignmentStatement.getStatements()) {
                    AssignmentExpression assignmentExpression = new AssignmentExpression(
                            assignmentStatement.getLValue(),
                            assignmentStatement.getRValue()
                    );
                    builder
                            .append(toString(assignmentExpression))
                            .append(", ");
                }

                // Удаляем лишние пробел и запятую в конце последнего присвоения
                if (builder.length() > 2) {
                    builder.deleteCharAt(builder.length() - 1);
                    builder.deleteCharAt(builder.length() - 1);
                }

                yield builder.toString();
            }
            default -> throw new IllegalStateException("Unexpected value: " + init);
        };
    }

    public String toString(GeneralForLoop generalForLoop) {
        StringBuilder builder = new StringBuilder();

        builder.append("for (");

        boolean addSemi = true;
        if (generalForLoop.hasInitializer()) {
            String init = toString(generalForLoop.getInitializer());
            if (init.stripTrailing().endsWith(";")) {
                addSemi = false;
            }
            builder.append(init);
        }
        if (addSemi) {
            builder.append("; ");
        }
        else {
            builder.append(" ");
        }

        if (generalForLoop.hasCondition()) {
            String condition = toString(generalForLoop.getCondition());
            builder.append(condition);
        }
        builder.append("; ");

        if (generalForLoop.hasUpdate()) {
            String update = toString(generalForLoop.getUpdate());
            builder.append(update);
        }

        Statement body = generalForLoop.getBody();
        if (body instanceof CompoundStatement compoundStatement) {
            builder.append(")");

            if (_openBracketOnSameLine) {
                builder
                        .append(" ")
                        .append(toString(compoundStatement));
            }
            else {
                builder.append("\n");
                builder.append(indent(toString(body)));
            }
        }
        else {
            builder.append(")\n");
            increaseIndentLevel();
            builder.append(indent(toString(body)));
            decreaseIndentLevel();
        }

        return builder.toString();
    }

    private String getForRangeUpdate(RangeForLoop forRangeLoop) {
        if (forRangeLoop.getRange().getType() == Range.Type.UP) {
            long stepValue = forRangeLoop.getStepValueAsLong();

            if (stepValue == 1) {
                return String.format("%s++", toString(forRangeLoop.getIdentifier()));
            }
            else {
                return String.format("%s += %d", toString(forRangeLoop.getIdentifier()), stepValue);
            }
        }
        else if (forRangeLoop.getRange().getType() == Range.Type.DOWN) {
            long stepValue = forRangeLoop.getStepValueAsLong();

            if (stepValue == 1) {
                return String.format("%s--", toString(forRangeLoop.getIdentifier()));
            }
            else {
                return String.format("%s -= %d", toString(forRangeLoop.getIdentifier()), stepValue);
            }
        }

        throw new MeaningTreeException("Can't determine range type in for loop");
    }

    private String getForRangeHeader(RangeForLoop forRangeLoop) {
        if (forRangeLoop.getRange().getType() == Range.Type.UP) {
            String header = "int %s = %s; %s %s %s; %s";
            String compOperator = forRangeLoop.isExcludingStop() ? "<" : "<=";
            return header.formatted(
                    toString(forRangeLoop.getIdentifier()),
                    toString(forRangeLoop.getStart()),
                    toString(forRangeLoop.getIdentifier()),
                    compOperator,
                    toString(forRangeLoop.getStop()),
                    getForRangeUpdate(forRangeLoop)
            );
        }
        else if (forRangeLoop.getRange().getType() == Range.Type.DOWN) {
            String header = "int %s = %s; %s %s %s; %s";
            String compOperator = forRangeLoop.isExcludingStop() ? ">" : ">=";
            return header.formatted(
                    toString(forRangeLoop.getIdentifier()),
                    toString(forRangeLoop.getStart()),
                    toString(forRangeLoop.getIdentifier()),
                    compOperator,
                    toString(forRangeLoop.getStop()),
                    getForRangeUpdate(forRangeLoop)
            );
        }

        throw new MeaningTreeException("Can't determine range type in for loop");
    }

    public String toString(RangeForLoop forRangeLoop) {
        StringBuilder builder = new StringBuilder();

        String header = "for (" + getForRangeHeader(forRangeLoop) + ")";
        builder.append(header);

        Statement body = forRangeLoop.getBody();
        if (body instanceof CompoundStatement compoundStatement) {
            if (_openBracketOnSameLine) {
                builder
                        .append(" ")
                        .append(toString(compoundStatement));
            }
            else {
                builder.append("\n");
                builder.append(indent(toString(body)));
            }
        }
        else {
            builder.append("\n");
            increaseIndentLevel();
            builder.append(indent(toString(body)));
            decreaseIndentLevel();
        }

        return builder.toString();
    }

    private String makeSimpleJavaProgram(List<Node> nodes) {
        StringBuilder builder = new StringBuilder();

        builder.append("package main;\n\n");

        builder.append("public class Main {\n\n");
        increaseIndentLevel();

        builder.append(
                indent("public static void main(String[] args) {\n")
        );
        increaseIndentLevel();

        for (Node node : nodes) {
            builder.append(
                    indent("%s\n".formatted(toString(node)))
            );
        }
        decreaseIndentLevel();

        builder.append(indent("}\n"));
        decreaseIndentLevel();

        builder.append("}");

        return builder.toString();
    }

    public String toString(ProgramEntryPoint entryPoint) {
        List<Node> nodes = entryPoint.getBody();
        for (var node : nodes) {
            if (node instanceof Statement statement) {
                HindleyMilner.inference(statement, _typeScope);
            }
        }

        if (!entryPoint.hasMainClass()
                //&& getConfigParameter("translationUnitMode").getBooleanValue()
        ) {
            return makeSimpleJavaProgram(nodes);
        }

        StringBuilder builder = new StringBuilder();
        for (Node node : nodes) {
            builder.append("%s\n".formatted(toString(node)));
        }

        return builder.toString();
    }

    public String toString(ScopedIdentifier scopedIdent) {
        StringBuilder builder = new StringBuilder();

        for (var ident : scopedIdent.getScopeResolution()) {
            builder.append(toString(ident)).append(".");
        }
        builder.deleteCharAt(builder.length() - 1); // Удаляем последнюю точку

        return builder.toString();
    }

    public String toString(QualifiedIdentifier scopedIdent) {
        StringBuilder builder = new StringBuilder();
        builder.append(toString(scopedIdent.getScope()));
        builder.append("::");
        builder.append(toString(scopedIdent.getMember()));
        return builder.toString();
    }

    public String toString(FunctionCall funcCall) {
        StringBuilder builder = new StringBuilder();

        builder.append(toString(funcCall.getFunction())).append("(");
        for (Expression expr : funcCall.getArguments()) {
            builder.append(toString(expr)).append(", ");
        }

        if (!funcCall.getArguments().isEmpty()) {
            // Удаляем два последних символа - запятую и пробел
            builder.deleteCharAt(builder.length() - 1);
            builder.deleteCharAt(builder.length() - 1);
        }
        builder.append(")");

        return builder.toString();
    }

    public String toString(WhileLoop whileLoop) {
        String header = "while (" + toString(whileLoop.getCondition()) + ")";

        Statement body = whileLoop.getBody();
        if (body instanceof CompoundStatement compStmt) {
            return header + (_openBracketOnSameLine ? " " : "\n") + toString(compStmt);
        }
        else {
            increaseIndentLevel();
            String result = header + "\n" + indent(toString(body));
            decreaseIndentLevel();
            return result;
        }
    }

    private String toString(PostfixIncrementOp inc) {
        return toString(inc.getArgument()) + "++";
    }

    private String toString(PostfixDecrementOp dec) {
        return toString(dec.getArgument()) + "--";
    }

    private String toString(PrefixIncrementOp inc) {
        return "++" + toString(inc.getArgument());
    }

    private String toString(PrefixDecrementOp dec) {
        return "--" + toString(dec.getArgument());
    }

    private String toString(PowOp op) {
        return "Math.pow(%s, %s)".formatted(toString(op.getLeft()), toString(op.getRight()));
    }

    private String toString(PackageDeclaration decl) {
        return "package %s;".formatted(toString(decl.getPackageName()));
    }
}
