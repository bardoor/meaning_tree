package org.vstu.meaningtree.languages;

import org.treesitter.TSNode;
import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Node;
import org.vstu.meaningtree.nodes.expressions.BinaryExpression;
import org.vstu.meaningtree.nodes.expressions.ParenthesizedExpression;
import org.vstu.meaningtree.nodes.expressions.UnaryExpression;
import org.vstu.meaningtree.nodes.expressions.bitwise.*;
import org.vstu.meaningtree.nodes.expressions.calls.FunctionCall;
import org.vstu.meaningtree.nodes.expressions.calls.MethodCall;
import org.vstu.meaningtree.nodes.expressions.comparison.*;
import org.vstu.meaningtree.nodes.expressions.identifiers.SimpleIdentifier;
import org.vstu.meaningtree.nodes.expressions.logical.NotOp;
import org.vstu.meaningtree.nodes.expressions.logical.ShortCircuitAndOp;
import org.vstu.meaningtree.nodes.expressions.logical.ShortCircuitOrOp;
import org.vstu.meaningtree.nodes.expressions.math.*;
import org.vstu.meaningtree.nodes.expressions.other.*;
import org.vstu.meaningtree.nodes.expressions.unary.*;
import org.vstu.meaningtree.nodes.statements.ExpressionSequence;
import org.vstu.meaningtree.nodes.statements.ExpressionStatement;
import org.vstu.meaningtree.nodes.statements.assignments.AssignmentStatement;
import org.vstu.meaningtree.utils.TreeSitterUtils;
import org.vstu.meaningtree.utils.tokens.*;

import java.util.*;

public class JavaTokenizer extends LanguageTokenizer {
    private static final List<String> stopNodes = List.of("string_literal");
    private Set<Integer> valueSetNodes = new HashSet<>();

    private static final Map<String, OperatorToken> operators = new HashMap<>() {{
        put("CALL_(", new OperatorToken("(", TokenType.CALL_OPENING_BRACE, 1, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));
        put("CALL_)", new OperatorToken(")", TokenType.CALL_CLOSING_BRACE, 1, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));
        put("[", new OperatorToken("[", TokenType.SUBSCRIPT_OPENING_BRACE, 1, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));
        put("]", new OperatorToken("]", TokenType.SUBSCRIPT_CLOSING_BRACE, 1, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));
        put(".", new OperatorToken(".", TokenType.OPERATOR, 1, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));

        put("++", new OperatorToken("++", TokenType.OPERATOR, 2, OperatorAssociativity.LEFT, OperatorArity.UNARY, false, OperatorTokenPosition.POSTFIX));   // Постфиксный инкремент
        put("--", new OperatorToken("--", TokenType.OPERATOR, 2, OperatorAssociativity.LEFT, OperatorArity.UNARY, false, OperatorTokenPosition.POSTFIX));   // Постфиксный декремент

        put("+U", new OperatorToken("+", TokenType.OPERATOR, 3, OperatorAssociativity.RIGHT, OperatorArity.UNARY, false));   // Унарный плюс
        put("-U", new OperatorToken("-", TokenType.OPERATOR, 3, OperatorAssociativity.RIGHT, OperatorArity.UNARY, false));   // Унарный минус
        put("++U", new OperatorToken("++", TokenType.OPERATOR, 3, OperatorAssociativity.RIGHT, OperatorArity.UNARY, false)); // Префиксный инкремент
        put("--U", new OperatorToken("--", TokenType.OPERATOR, 3, OperatorAssociativity.RIGHT, OperatorArity.UNARY, false)); // Префиксный декремент
        put("!", new OperatorToken("!", TokenType.OPERATOR, 3, OperatorAssociativity.RIGHT, OperatorArity.UNARY, false));     // Логическое НЕ
        put("~", new OperatorToken("~", TokenType.OPERATOR, 3, OperatorAssociativity.RIGHT, OperatorArity.UNARY, false));     // Побитовая инверсия

        put("*", new OperatorToken("*", TokenType.OPERATOR, 4, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));     // Умножение
        put("/", new OperatorToken("/", TokenType.OPERATOR, 4, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));     // Деление
        put("%", new OperatorToken("%", TokenType.OPERATOR, 4, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));     // Остаток от деления

        put("+", new OperatorToken("+", TokenType.OPERATOR, 5, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));     // Сложение (бинарный)
        put("-", new OperatorToken("-", TokenType.OPERATOR, 5, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));     // Вычитание (бинарный)

        put("<<", new OperatorToken("<<", TokenType.OPERATOR, 6, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));   // Левый сдвиг
        put(">>", new OperatorToken(">>", TokenType.OPERATOR, 6, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));   // Правый сдвиг
        put(">>>", new OperatorToken(">>>", TokenType.OPERATOR, 6, OperatorAssociativity.LEFT, OperatorArity.BINARY, false)); // Беззнаковый правый сдвиг

        put("<", new OperatorToken("<", TokenType.OPERATOR, 7, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));     // Меньше
        put("<=", new OperatorToken("<=", TokenType.OPERATOR, 7, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));   // Меньше или равно
        put(">", new OperatorToken(">", TokenType.OPERATOR, 7, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));     // Больше
        put(">=", new OperatorToken(">=", TokenType.OPERATOR, 7, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));   // Больше или равно
        put("instanceof", new OperatorToken("instanceof", TokenType.OPERATOR, 7, OperatorAssociativity.LEFT, OperatorArity.BINARY, false)); // Проверка на тип

        put("==", new OperatorToken("==", TokenType.OPERATOR, 8, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));   // Равно
        put("!=", new OperatorToken("!=", TokenType.OPERATOR, 8, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));   // Не равно

        put("&", new OperatorToken("&", TokenType.OPERATOR, 9, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));     // Побитовое И
        put("^", new OperatorToken("^", TokenType.OPERATOR, 10, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));    // Побитовое исключающее ИЛИ
        put("|", new OperatorToken("|", TokenType.OPERATOR, 11, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));    // Побитовое ИЛИ

        put("&&", new OperatorToken("&&", TokenType.OPERATOR, 12, OperatorAssociativity.LEFT, OperatorArity.BINARY, true));  // Логическое И
        put("||", new OperatorToken("||", TokenType.OPERATOR, 13, OperatorAssociativity.LEFT, OperatorArity.BINARY, true));  // Логическое ИЛИ

        List<OperatorToken> ternary = OperatorToken.makeTernary(14, OperatorAssociativity.RIGHT, true, "?", ":");
        put("?", ternary.getFirst());  // Тернарный оператор
        put(":", ternary.getLast());

        put("=", new OperatorToken("=", TokenType.OPERATOR, 15, OperatorAssociativity.RIGHT, OperatorArity.BINARY, false));   // Присваивание
        put("+=", new OperatorToken("+=", TokenType.OPERATOR, 15, OperatorAssociativity.RIGHT, OperatorArity.BINARY, false)); // Сложение с присваиванием
        put("-=", new OperatorToken("-=", TokenType.OPERATOR, 15, OperatorAssociativity.RIGHT, OperatorArity.BINARY, false)); // Вычитание с присваиванием
        put("*=", new OperatorToken("*=", TokenType.OPERATOR, 15, OperatorAssociativity.RIGHT, OperatorArity.BINARY, false)); // Умножение с присваиванием
        put("/=", new OperatorToken("/=", TokenType.OPERATOR, 15, OperatorAssociativity.RIGHT, OperatorArity.BINARY, false)); // Деление с присваиванием
        put("%=", new OperatorToken("%=", TokenType.OPERATOR, 15, OperatorAssociativity.RIGHT, OperatorArity.BINARY, false)); // Остаток с присваиванием
        put("<<=", new OperatorToken("<<=", TokenType.OPERATOR, 15, OperatorAssociativity.RIGHT, OperatorArity.BINARY, false)); // Левый сдвиг с присваиванием
        put(">>=", new OperatorToken(">>=", TokenType.OPERATOR, 15, OperatorAssociativity.RIGHT, OperatorArity.BINARY, false)); // Правый сдвиг с присваиванием
        put(">>>=", new OperatorToken(">>>=", TokenType.OPERATOR, 15, OperatorAssociativity.RIGHT, OperatorArity.BINARY, false)); // Беззнаковый правый сдвиг с присваиванием
        put("&=", new OperatorToken("&=", TokenType.OPERATOR, 15, OperatorAssociativity.RIGHT, OperatorArity.BINARY, false)); // Побитовое И с присваиванием
        put("^=", new OperatorToken("^=", TokenType.OPERATOR, 15, OperatorAssociativity.RIGHT, OperatorArity.BINARY, false)); // Побитовое исключающее ИЛИ с присваиванием
        put("|=", new OperatorToken("|=", TokenType.OPERATOR, 15, OperatorAssociativity.RIGHT, OperatorArity.BINARY, false)); // Побитовое ИЛИ с присваиванием
    }};


    public JavaTokenizer(JavaLanguage javaLanguage, JavaViewer viewer) {
        super(javaLanguage, viewer);
    }

    @Override
    protected List<String> getStopNodes() {
        return stopNodes;
    }

    @Override
    public OperatorToken getOperatorByTokenName(String tokenName) {
        return operators.getOrDefault(tokenName, null);
    }

    @Override
    protected OperatorToken getOperator(String tokenValue, TSNode node) {
        if (!node.getParent().isNull() && node.getParent().getType().equals("unary_expression") && List.of("+", "-").contains(tokenValue)) {
            if (tokenValue.equals("+")) {
                return operators.get("UPLUS").clone();
            } else if (tokenValue.equals("-")) {
                return operators.get("UMINUS").clone();
            }
        }

        if (!node.getParent().isNull() && tokenValue.equals("(") && !node.getParent().isNull() && node.getParent().getType().equals("method_invocation")) {
            return operators.get("CALL_(").clone();
        }

        if (!node.getParent().isNull() && tokenValue.equals(")") && !node.getParent().isNull() && node.getParent().getType().equals("method_invocation")) {
            return operators.get("CALL_)").clone();
        }

        if (!node.getParent().isNull() && node.getParent().getType().equals("update_expression") &&
                TreeSitterUtils.getCodePiece(code, node.getParent()).startsWith("++") && tokenValue.equals("++")) {
            return operators.get("++U").clone();
        }

        if (!node.getParent().isNull() && node.getParent().getType().equals("update_expression") &&
                TreeSitterUtils.getCodePiece(code, node.getParent()).startsWith("--")  && tokenValue.equals("--")) {
            return operators.get("--U").clone();
        }

        OperatorToken tok = operators.getOrDefault(tokenValue, null);
        return tok == null ? null : tok.clone();
    }

    @Override
    protected Token recognizeToken(TSNode node) {
        String type = node.getType();
        TSNode parent = node.getParent();
        OperatorToken recognizedOperator = getOperator(type, node);

        if (recognizedOperator != null) {
            return recognizedOperator;
        }

        TokenType tokenType;

        if (type.equals("{")) {
            if (parent.getType().equals("array_initializer")) {
                tokenType = TokenType.INITIALIZER_LIST_OPENING_BRACE;
            } else {
                tokenType = TokenType.COMPOUND_OPENING_BRACE;
            }
        } else if (type.equals("}")) {
            if (parent.getType().equals("array_initializer")) {
                tokenType = TokenType.INITIALIZER_LIST_CLOSING_BRACE;
            } else {
                tokenType = TokenType.COMPOUND_CLOSING_BRACE;
            }
        } else if (type.equals("(")) {
            tokenType = TokenType.OPENING_BRACE;
        } else if (type.equals(")")) {
            tokenType = TokenType.CLOSING_BRACE;
        } else if (type.equals(";")) {
            tokenType = TokenType.SEPARATOR;
        } else if (List.of("type_identifier", "identifier",
                "integral_type", "boolean_type").contains(type)) {
            if (!parent.isNull() && parent.getType().equals("method_invocation")) {
                tokenType = TokenType.CALLABLE_IDENTIFIER;
            } else {
                tokenType = TokenType.IDENTIFIER;
            }
        } else if (List.of("decimal_floating_point_literal", "decimal_integer_literal",
                "string_literal", "true", "false", "null_literal"
        ).contains(type)) {
            tokenType = TokenType.CONST;
        } else if (type.equals(",")) {
            tokenType = TokenType.COMMA;
        } else {
            tokenType = TokenType.UNKNOWN;
        }

        return new Token(TreeSitterUtils.getCodePiece(code, node), tokenType);
    }

    @Override
    protected List<String> getOperatorNodes(OperatorArity arity) {
        return switch (arity) {
            case UNARY -> List.of(
                    "unary_expression", "update_expression"
            );
            case BINARY -> List.of("binary_expression", "field_access", "cast_expression");
            case TERNARY -> List.of("ternary_expression");
        };
    }

    @Override
    protected String getFieldNameByOperandPos(OperandPosition pos, String operatorNode) {
        if (List.of(
                "unary_expression", "update_expression"
        ).contains(operatorNode)) {
            return "argument";
        } else if (operatorNode.equals("binary_expression")) {
            if (pos == OperandPosition.LEFT) {
                return "left";
            } else if (pos == OperandPosition.RIGHT) {
                return "right";
            }
        } else if (operatorNode.equals("field_access")) {
            if (pos == OperandPosition.LEFT) {
                return "object";
            } else if (pos == OperandPosition.RIGHT) {
                return "field";
            }
        } else if (operatorNode.equals("cast_expression")) {
            if (pos == OperandPosition.LEFT) {
                return "type";
            } else if (pos == OperandPosition.RIGHT) {
                return "value";
            }
        } else if (operatorNode.equals("ternary_expression")) {
            if (pos == OperandPosition.LEFT) {
                return "condition";
            } else if (pos == OperandPosition.CENTER) {
                return "consequence";
            } else if (pos == OperandPosition.RIGHT) {
                return "alternative";
            }
        }
        return null;
    }

    @Override
    public TokenList tokenize(Node node) {
        TokenList result = new TokenList();
        tokenize(node, result);
        return result;
    }

    public TokenGroup tokenize(Node node, TokenList result) {
        int posStart = result.size();
        switch (node) {
            case BinaryExpression binOp -> tokenizeBinary(binOp, result);
            case UnaryExpression unaryOp -> tokenizeUnary(unaryOp, result);
            case FunctionCall call -> tokenizeCall(call, result);
            case MemberAccess access -> tokenizeFieldOp(access, result);
            case CompoundComparison comparison -> tokenizeCompoundComparison(comparison, result);
            case IndexExpression subscript -> tokenizeSubscript(subscript, result);
            case TernaryOperator ternary -> tokenizeTernary(ternary, result);
            case ParenthesizedExpression paren -> {
                result.add(new Token("(", TokenType.OPENING_BRACE));
                tokenize(paren.getExpression(), result);
                result.add(new Token(")", TokenType.CLOSING_BRACE));
            }
            case AssignmentExpression assignment -> {
                tokenize(assignment.getLValue(), result);
                result.add(getOperatorByTokenName("="));
                tokenize(assignment.getRValue(), result);
            }
            case AssignmentStatement assignment -> {
                tokenize(assignment.getLValue(), result);
                result.add(getOperatorByTokenName("="));
                tokenize(assignment.getRValue(), result);
            }
            case ExpressionSequence sequence -> {
                for (Expression expr : sequence.getExpressions()) {
                    tokenize(expr, result);
                    result.add(new Token(",", TokenType.COMMA));
                }
                if (!sequence.getExpressions().isEmpty()) result.removeLast();
            }
            case ExpressionStatement exprStmt -> {
                tokenize(exprStmt.getExpression(), result);
            }
            default ->  {
                String s = viewer.toString(node);
                result.addAll(tokenize(s));
            }
        }
        int posStop = result.size();
        TokenGroup resultGroup = new TokenGroup(posStart, posStop, result);
        if (node.getAssignedValueTag() != null && !valueSetNodes.contains(node.getId())) {
            resultGroup.assignValue(node.getAssignedValueTag());
            valueSetNodes.add(node.getId());
        }
        return resultGroup;
    }

    private void tokenizeCall(FunctionCall call, TokenList result) {
        if (call instanceof MethodCall method) {
            tokenize(new MemberAccess(method.getObject(), (SimpleIdentifier) method.getFunction()), result);
        } else {
            tokenize(call.getFunction(), result);
        }
        OperatorToken tok = getOperatorByTokenName("CALL_(");
        result.add(tok);
        int i = 0;
        for (Expression expr : call.getArguments()) {
            TokenGroup operand = tokenize(expr, result);
            result.add(new Token(",", TokenType.COMMA));
            if (i == 0) {
                operand.setMetadata(tok, OperandPosition.LEFT);
            } else if (i == call.getArguments().size() - 1) {
                operand.setMetadata(tok, OperandPosition.RIGHT);
            } else {
                operand.setMetadata(tok, OperandPosition.CENTER);
            }
            i++;
        }
        if (!call.getArguments().isEmpty()) result.removeLast();
        result.add(getOperatorByTokenName("CALL_)"));
        if (call.getAssignedValueTag() != null) {
            tok.assignValue(call.getAssignedValueTag());
            valueSetNodes.add(call.getId());
        }
    }

    private void tokenizeUnary(UnaryExpression unaryOp, TokenList result) {
        String operator = switch (unaryOp) {
            case NotOp op -> "!";
            case InversionOp op -> "~";
            case UnaryMinusOp op -> "-U";
            case UnaryPlusOp op -> "+U";
            case PostfixIncrementOp op -> "++";
            case PrefixIncrementOp op -> "++U";
            case PostfixDecrementOp op -> "--";
            case PrefixDecrementOp op -> "--U";
            default -> null;
        };
        if (operator == null) {
            String s = viewer.toString(unaryOp);
            result.addAll(tokenize(s));
            return;
        }
        TokenGroup op;
        OperatorToken token;
        if (unaryOp instanceof PostfixDecrementOp || unaryOp instanceof PostfixIncrementOp) {
            op = tokenize(unaryOp.getArgument(), result);
            token = getOperatorByTokenName(operator);
            result.add(token);
            op.setMetadata(token, OperandPosition.LEFT);
        } else {
            token = getOperatorByTokenName(operator);
            result.add(token);
            op = tokenize(unaryOp.getArgument(), result);
            op.setMetadata(token, OperandPosition.RIGHT);
        }
        if (unaryOp.getAssignedValueTag() != null) {
            token.assignValue(unaryOp.getAssignedValueTag());
            valueSetNodes.add(unaryOp.getId());
        }
    }

    private void tokenizeBinary(BinaryExpression binOp, TokenList result) {
        String operator = switch (binOp) {
            case AddOp op -> "+";
            case SubOp op -> "-";
            case MulOp op -> "*";
            case DivOp op -> "/";
            case LtOp op -> "<";
            case GtOp op -> ">";
            case NotEqOp op -> "!=";
            case GeOp op -> ">=";
            case LeOp op -> "<=";
            case ShortCircuitAndOp op -> "&&";
            case ShortCircuitOrOp op -> "||";
            case BitwiseAndOp op -> "&";
            case BitwiseOrOp op -> "|";
            case XorOp op -> "^";
            case LeftShiftOp op -> "<<";
            case RightShiftOp op -> ">>";
            case EqOp op -> "==";
            case ModOp op -> "%";
            case InstanceOfOp op -> "instanceof";
            default -> null;
        };
        if (operator == null) {
            String s = viewer.toString(binOp);
            result.addAll(tokenize(s));
            return;
        }
        TokenGroup left = tokenize(binOp.getLeft(), result);
        OperatorToken token = getOperatorByTokenName(operator);
        result.add(token);
        TokenGroup right = tokenize(binOp.getRight(), result);
        left.setMetadata(token, OperandPosition.LEFT);
        right.setMetadata(token, OperandPosition.RIGHT);
        if (binOp.getAssignedValueTag() != null) {
            token.assignValue(binOp.getAssignedValueTag());
            valueSetNodes.add(binOp.getId());
        }
    }

    private void tokenizeFieldOp(MemberAccess access, TokenList result) {
        TokenGroup base = tokenize(access.getExpression(), result);
        OperatorToken dot = getOperatorByTokenName(".");
        result.add(dot);
        TokenGroup member = tokenize(access.getMember(), result);
        base.setMetadata(dot, OperandPosition.LEFT);
        member.setMetadata(dot, OperandPosition.RIGHT);
        if (access.getAssignedValueTag() != null) {
            dot.assignValue(access.getAssignedValueTag());
            valueSetNodes.add(access.getId());
        }
    }

    private void tokenizeTernary(TernaryOperator ternary, TokenList result) {
        TokenGroup cond = tokenize(ternary.getCondition(), result);
        OperatorToken op1 = getOperatorByTokenName("?");
        cond.setMetadata(op1, OperandPosition.LEFT);
        result.add(op1);
        TokenGroup alt1 = tokenize(ternary.getThenExpr(), result);
        alt1.setMetadata(op1, OperandPosition.CENTER);
        OperatorToken op2 = getOperatorByTokenName(":");
        result.add(op2);
        TokenGroup alt2 = tokenize(ternary.getElseExpr(), result);
        alt2.setMetadata(op2, OperandPosition.RIGHT);
        if (ternary.getAssignedValueTag() != null) {
            op1.assignValue(ternary.getAssignedValueTag());
            valueSetNodes.add(ternary.getId());
        }
    }

    private void tokenizeCompoundComparison(CompoundComparison comparison, TokenList result) {
        if (comparison.getComparisons().size() == 1) {
            tokenize(comparison.getComparisons().getFirst(), result);
            return;
        }
        ShortCircuitAndOp op = new ShortCircuitAndOp(comparison.getComparisons().getFirst(),
                comparison.getComparisons().get(1));
        for (int i = 2; i < comparison.getComparisons().size(); i++) {
            op = new ShortCircuitAndOp(op, comparison.getComparisons().get(i));
        }
        if (comparison.getAssignedValueTag() != null) {
            op.setAssignedValueTag(comparison.getAssignedValueTag());
        }
        tokenize(op, result);
    }

    private void tokenizeSubscript(IndexExpression subscript, TokenList result) {
        TokenGroup leftOperand = tokenize(subscript.getExpr(), result);
        OperatorToken open = getOperatorByTokenName("[");
        result.add(open);
        leftOperand.setMetadata(open, OperandPosition.LEFT);
        TokenGroup centerOperand = tokenize(subscript.getIndex(), result);
        centerOperand.setMetadata(open, OperandPosition.CENTER);
        result.add(getOperatorByTokenName("]"));
    }
}
