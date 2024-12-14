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
import org.vstu.meaningtree.nodes.expressions.identifiers.QualifiedIdentifier;
import org.vstu.meaningtree.nodes.expressions.identifiers.ScopedIdentifier;
import org.vstu.meaningtree.nodes.expressions.identifiers.SimpleIdentifier;
import org.vstu.meaningtree.nodes.expressions.logical.NotOp;
import org.vstu.meaningtree.nodes.expressions.logical.ShortCircuitAndOp;
import org.vstu.meaningtree.nodes.expressions.logical.ShortCircuitOrOp;
import org.vstu.meaningtree.nodes.expressions.math.*;
import org.vstu.meaningtree.nodes.expressions.other.*;
import org.vstu.meaningtree.nodes.expressions.pointers.PointerMemberAccess;
import org.vstu.meaningtree.nodes.expressions.pointers.PointerPackOp;
import org.vstu.meaningtree.nodes.expressions.pointers.PointerUnpackOp;
import org.vstu.meaningtree.nodes.expressions.unary.*;
import org.vstu.meaningtree.nodes.statements.ExpressionStatement;
import org.vstu.meaningtree.nodes.statements.assignments.AssignmentStatement;
import org.vstu.meaningtree.utils.NodeLabel;
import org.vstu.meaningtree.utils.TreeSitterUtils;
import org.vstu.meaningtree.utils.tokens.*;

import java.util.*;

public class CppTokenizer extends LanguageTokenizer {
    private static final List<String> stopNodes = List.of("user_defined_literal");
    private Set<Long> valueSetNodes = new HashSet<>();

    protected static final Map<String, OperatorToken> operators = new HashMap<>() {{
        put("::", new OperatorToken("::", TokenType.OPERATOR, 1, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));

        List<OperatorToken> braces = OperatorToken.makeComplex(2,
                OperatorArity.BINARY, OperatorAssociativity.LEFT, false,
                new String[] {"(", ")"},
                new TokenType[] {TokenType.CALL_OPENING_BRACE, TokenType.CALL_CLOSING_BRACE},
                new OperatorTokenPosition[]{OperatorTokenPosition.AROUND, OperatorTokenPosition.AROUND});

        put("CALL_(", braces.getFirst());
        put("CALL_)", braces.getLast());

        List<OperatorToken> subscript = OperatorToken.makeComplex(2,
                OperatorArity.BINARY, OperatorAssociativity.LEFT, false,
                new String[] {"[", "]"},
                new TokenType[] {TokenType.SUBSCRIPT_OPENING_BRACE, TokenType.SUBSCRIPT_CLOSING_BRACE},
                new OperatorTokenPosition[]{OperatorTokenPosition.AROUND, OperatorTokenPosition.AROUND});

        put("[", subscript.getFirst());
        put("]", subscript.getLast());

        put("->", new OperatorToken("->", TokenType.OPERATOR, 2, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));
        put(".", new OperatorToken(".", TokenType.OPERATOR, 2, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));
        put("++", new OperatorToken("++", TokenType.OPERATOR, 2, OperatorAssociativity.LEFT, OperatorArity.UNARY, false));
        put("--", new OperatorToken("--", TokenType.OPERATOR, 2, OperatorAssociativity.LEFT, OperatorArity.UNARY, false));
        put("++U", new OperatorToken("++", TokenType.OPERATOR, 3, OperatorAssociativity.RIGHT, OperatorArity.UNARY, false).setFirstOperandToEvaluation(OperandPosition.RIGHT));
        put("--U", new OperatorToken("--", TokenType.OPERATOR, 3, OperatorAssociativity.RIGHT, OperatorArity.UNARY, false).setFirstOperandToEvaluation(OperandPosition.RIGHT));
        put("UMINUS", new OperatorToken("-", TokenType.OPERATOR, 3, OperatorAssociativity.RIGHT, OperatorArity.UNARY, false));
        put("UPLUS", new OperatorToken("+", TokenType.OPERATOR, 3, OperatorAssociativity.RIGHT, OperatorArity.UNARY, false));
        put("POINTER_&", new OperatorToken("&", TokenType.OPERATOR, 3, OperatorAssociativity.RIGHT, OperatorArity.UNARY, false));
        put("POINTER_*", new OperatorToken("*", TokenType.OPERATOR, 3, OperatorAssociativity.RIGHT, OperatorArity.UNARY, false));
        put("~", new OperatorToken("~", TokenType.OPERATOR, 3, OperatorAssociativity.RIGHT, OperatorArity.UNARY, false));
        put("!", new OperatorToken("!", TokenType.OPERATOR, 3, OperatorAssociativity.RIGHT, OperatorArity.UNARY, false));
        put("sizeof", new OperatorToken("sizeof", TokenType.OPERATOR, 3, OperatorAssociativity.RIGHT, OperatorArity.UNARY, false));
        put("new", new OperatorToken("new", TokenType.OPERATOR, 3, OperatorAssociativity.RIGHT, OperatorArity.UNARY, false));
        put("delete", new OperatorToken("delete", TokenType.OPERATOR, 3, OperatorAssociativity.RIGHT, OperatorArity.UNARY, false));

        put("*", new OperatorToken("*", TokenType.OPERATOR, 5, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));
        put("/", new OperatorToken("/", TokenType.OPERATOR, 5, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));
        put("%", new OperatorToken("%", TokenType.OPERATOR, 5, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));

        put("+", new OperatorToken("+", TokenType.OPERATOR, 6, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));
        put("-", new OperatorToken("-", TokenType.OPERATOR, 6, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));

        put("<<", new OperatorToken("<<", TokenType.OPERATOR, 7, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));
        put(">>", new OperatorToken(">>", TokenType.OPERATOR, 7, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));

        put("<=>", new OperatorToken("<=>", TokenType.OPERATOR, 8, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));

        put("<", new OperatorToken("<", TokenType.OPERATOR, 9, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));
        put("<=", new OperatorToken("<=", TokenType.OPERATOR, 9, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));
        put(">", new OperatorToken(">", TokenType.OPERATOR, 9, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));
        put(">=", new OperatorToken(">=", TokenType.OPERATOR, 9, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));

        put("==", new OperatorToken("==", TokenType.OPERATOR, 10, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));
        put("!=", new OperatorToken("!=", TokenType.OPERATOR, 10, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));

        put("&", new OperatorToken("&", TokenType.OPERATOR, 11, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));

        put("^", new OperatorToken("^", TokenType.OPERATOR, 12, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));

        put("|", new OperatorToken("|", TokenType.OPERATOR, 13, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));

        put("&&", new OperatorToken("&&", TokenType.OPERATOR, 14, OperatorAssociativity.LEFT, OperatorArity.BINARY, true, OperatorTokenPosition.INFIX, OperatorType.AND));

        put("||", new OperatorToken("||", TokenType.OPERATOR, 15, OperatorAssociativity.LEFT, OperatorArity.BINARY, true, OperatorTokenPosition.INFIX, OperatorType.OR));

        List<OperatorToken> ternary = OperatorToken.makeComplex(16, OperatorArity.TERNARY,
                OperatorAssociativity.RIGHT, true, new String[] {"?", ":"},
                new TokenType[] {TokenType.OPERATOR, TokenType.OPERATOR}
        );
        put("?", ternary.getFirst().setFirstOperandToEvaluation(OperandPosition.LEFT));  // Тернарный оператор
        put(":", ternary.getLast().setFirstOperandToEvaluation(OperandPosition.LEFT));

        put("=", new OperatorToken("=", TokenType.OPERATOR, 16, OperatorAssociativity.RIGHT, OperatorArity.BINARY, false));
        put("+=", new OperatorToken("+=", TokenType.OPERATOR, 16, OperatorAssociativity.RIGHT, OperatorArity.BINARY, false));
        put("-=", new OperatorToken("-=", TokenType.OPERATOR, 16, OperatorAssociativity.RIGHT, OperatorArity.BINARY, false));
        put("*=", new OperatorToken("*=", TokenType.OPERATOR, 16, OperatorAssociativity.RIGHT, OperatorArity.BINARY, false));
        put("/=", new OperatorToken("/=", TokenType.OPERATOR, 16, OperatorAssociativity.RIGHT, OperatorArity.BINARY, false));
        put("%=", new OperatorToken("%=", TokenType.OPERATOR, 16, OperatorAssociativity.RIGHT, OperatorArity.BINARY, false));
        put("<<=", new OperatorToken("<<=", TokenType.OPERATOR, 16, OperatorAssociativity.RIGHT, OperatorArity.BINARY, false));
        put(">>=", new OperatorToken(">>=", TokenType.OPERATOR, 16, OperatorAssociativity.RIGHT, OperatorArity.BINARY, false));
        put("&=", new OperatorToken("&=", TokenType.OPERATOR, 16, OperatorAssociativity.RIGHT, OperatorArity.BINARY, false));
        put("^=", new OperatorToken("^=", TokenType.OPERATOR, 16, OperatorAssociativity.RIGHT, OperatorArity.BINARY, false));
        put("|=", new OperatorToken("|=", TokenType.OPERATOR, 16, OperatorAssociativity.RIGHT, OperatorArity.BINARY, false));

        put(",", new OperatorToken(",", TokenType.COMMA, 17, OperatorAssociativity.LEFT, OperatorArity.BINARY, false));
    }};


    public CppTokenizer(CppTranslator translator) {
        super(translator);
    }

    @Override
    protected List<String> getStopNodes() {
        return stopNodes;
    }

    @Override
    protected List<String> getOperatorNodes(OperatorArity arity) {
        return switch (arity) {
            case UNARY -> List.of(
                    "unary_expression", "update_expression", "pointer_expression"
            );
            case BINARY -> List.of("binary_expression", "field_expression", "cast_expression");
            case TERNARY -> List.of("conditional_expression");
        };
    }

    @Override
    protected String getFieldNameByOperandPos(OperandPosition pos, String operatorNode) {
        if (List.of(
                "unary_expression", "update_expression", "pointer_expression"
        ).contains(operatorNode)) {
            return "argument";
        } else if (operatorNode.equals("binary_expression")) {
            if (pos == OperandPosition.LEFT) {
                return "left";
            } else if (pos == OperandPosition.RIGHT) {
                return "right";
            }
        } else if (operatorNode.equals("field_expression")) {
            if (pos == OperandPosition.LEFT) {
                return "argument";
            } else if (pos == OperandPosition.RIGHT) {
                return "field";
            }
        } else if (operatorNode.equals("cast_expression")) {
            if (pos == OperandPosition.LEFT) {
                return "type";
            } else if (pos == OperandPosition.RIGHT) {
                return "value";
            }
        } else if (operatorNode.equals("conditional_expression")) {
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

    protected OperatorToken getOperator(String tokenValue, TSNode node) {
        if (!node.getParent().isNull() && node.getParent().getType().equals("unary_expression") && List.of("+", "-").contains(tokenValue)) {
            if (tokenValue.equals("+")) {
                return operators.get("UPLUS").clone();
            } else if (tokenValue.equals("-")) {
                return operators.get("UMINUS").clone();
            }
        }

        if (!node.getParent().isNull() && tokenValue.equals("(") && !node.getParent().isNull() && node.getParent().getType().equals("call_expression")) {
            return operators.get("CALL_(").clone();
        }

        if (!node.getParent().isNull() && tokenValue.equals(")") && !node.getParent().isNull() && node.getParent().getType().equals("call_expression")) {
            return operators.get("CALL_)").clone();
        }

        if (!node.getParent().isNull() && node.getParent().getType().equals("update_expression") &&
                TreeSitterUtils.getCodePiece(code, node.getParent()).startsWith("++") && tokenValue.equals("++")) {
            return operators.get("++U").clone();
        }

        if (!node.getParent().isNull() && node.getParent().getType().equals("update_expression") &&
                TreeSitterUtils.getCodePiece(code, node.getParent()).startsWith("--") && tokenValue.equals("--")) {
            return operators.get("--U").clone();
        }

        if (!node.getParent().isNull() && node.getParent().getType().equals("pointer_expression") && List.of("*", "&").contains(tokenValue)) {
            if (tokenValue.equals("*")) {
                return operators.get("POINTER_*").clone();
            } else if (tokenValue.equals("&")) {
                return operators.get("POINTER_&").clone();
            }
        }

        OperatorToken tok = operators.getOrDefault(tokenValue, null);
        return tok == null ? null : tok.clone();
    }

    @Override
    public OperatorToken getOperatorByTokenName(String tokenName) {
        return operators.getOrDefault(tokenName, null).clone();
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
            if (parent.getType().equals("initializer_list")) {
                tokenType = TokenType.INITIALIZER_LIST_OPENING_BRACE;
            } else {
                tokenType = TokenType.COMPOUND_OPENING_BRACE;
            }
        } else if (type.equals("}")) {
            if (parent.getType().equals("initializer_list")) {
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
        } else if (List.of("identifier", "namespace_identifier", "type_identifier", "field_identifier").contains(type)) {
            if (!parent.isNull() && parent.getType().equals("call_expression")) {
                tokenType = TokenType.CALLABLE_IDENTIFIER;
            } else {
                tokenType = TokenType.IDENTIFIER;
            }
        } else if (List.of("number_literal", "string_literal", "true", "false", "null", "user_defined_literal").contains(type)) {
            tokenType = TokenType.CONST;
        } else {
            tokenType = TokenType.UNKNOWN;
        }
        return new Token(TreeSitterUtils.getCodePiece(code, node), tokenType);
    }


    @Override
    public TokenList tokenizeExtended(Node node) {
        TokenList result = new TokenList();
        tokenizeExtended(node, result);
        return result;
    }

    public TokenGroup tokenizeExtended(Node node, TokenList result) {
        if (node.hasLabel(NodeLabel.DUMMY)) {
            return new TokenGroup(0, 0, result);
        }
        int posStart = result.size();
        switch (node) {
            case BinaryExpression binOp -> tokenizeBinary(binOp, result);
            case UnaryExpression unaryOp -> tokenizeUnary(unaryOp, result);
            case FunctionCall call -> tokenizeCall(call, result);
            case MemberAccess access -> tokenizeFieldOp(access, result);
            case CompoundComparison comparison -> tokenizeCompoundComparison(comparison, result);
            case IndexExpression subscript -> tokenizeSubscript(subscript, result);
            case TernaryOperator ternary -> tokenizeTernary(ternary, result);
            case SimpleIdentifier ident -> {
                result.add(new Token(ident.getName(), TokenType.IDENTIFIER));
            }
            case QualifiedIdentifier ident -> {
                tokenizeExtended(ident.getScope(), result);
                result.add(getOperatorByTokenName("::"));
                tokenizeExtended(ident.getMember(), result);
            }
            case ScopedIdentifier ident -> {
                for (SimpleIdentifier simple : ident.getScopeResolution()) {
                    tokenizeExtended(simple, result);
                    result.add(getOperatorByTokenName("."));
                }
                if (!ident.getScopeResolution().isEmpty()) {
                    result.removeLast();
                }
            }
            case ParenthesizedExpression paren -> {
                result.add(new Token("(", TokenType.OPENING_BRACE));
                tokenizeExtended(paren.getExpression(), result);
                result.add(new Token(")", TokenType.CLOSING_BRACE));
            }
            case AssignmentExpression assignment -> {
                tokenizeExtended(assignment.getLValue(), result);
                result.add(getOperatorByTokenName("="));
                tokenizeExtended(assignment.getRValue(), result);
            }
            case AssignmentStatement assignment -> {
                tokenizeExtended(assignment.getLValue(), result);
                result.add(getOperatorByTokenName("="));
                tokenizeExtended(assignment.getRValue(), result);
                result.add(new Token(";", TokenType.SEPARATOR));
            }
            case ExpressionSequence sequence -> {
                for (Expression expr : sequence.getExpressions()) {
                    tokenizeExtended(expr, result);
                    result.add(getOperatorByTokenName(","));
                }
                if (!sequence.getExpressions().isEmpty()) result.removeLast();
            }
            case ExpressionStatement exprStmt -> {
                tokenizeExtended(exprStmt.getExpression(), result);
                result.add(new Token(";", TokenType.SEPARATOR));
            }
            default ->  {
                String s = viewer.toString(node);
                TokenList tokens = tokenize(translator.prepareCode(s));
                result.addAll(tokens.subList(0, tokens.getLast().type == TokenType.SEPARATOR ? tokens.size() - 1 : tokens.size()));
            }
        }
        int posStop = result.size();
        TokenGroup resultGroup =  new TokenGroup(posStart, posStop, result);
        if (node.getAssignedValueTag() != null && !valueSetNodes.contains(node.getId())) {
            resultGroup.assignValue(node.getAssignedValueTag());
            valueSetNodes.add(node.getId());
        }
        return resultGroup;
    }

    private void tokenizeCompoundComparison(CompoundComparison comparison, TokenList result) {
        if (comparison.getComparisons().size() == 1) {
            tokenizeExtended(comparison.getComparisons().getFirst(), result);
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
        tokenizeExtended(op, result);
    }

    private void tokenizeSubscript(IndexExpression subscript, TokenList result) {
        TokenGroup leftOperand = tokenizeExtended(subscript.getExpr(), result);
        OperatorToken open = getOperatorByTokenName("[");
        result.add(open);
        leftOperand.setMetadata(open, OperandPosition.LEFT);
        TokenGroup centerOperand = tokenizeExtended(subscript.getIndex(), result);
        centerOperand.setMetadata(open, OperandPosition.CENTER);
        result.add(getOperatorByTokenName("]"));
    }

    private void tokenizeCall(FunctionCall call, TokenList result) {
        TokenGroup complexName = null;
        if (call instanceof MethodCall method) {
            complexName = tokenizeExtended(method.getObject(), result);
            result.add(new Token(".", TokenType.SEPARATOR));
            tokenizeExtended(method.getFunction(), result);
        } else {
            tokenizeExtended(call.getFunction(), result);
        }
        OperatorToken tok = getOperatorByTokenName("CALL_(");
        if (complexName != null) complexName.setMetadata(tok, OperandPosition.LEFT);
        result.add(tok);
        for (Expression expr : call.getArguments()) {
            TokenGroup operand = tokenizeExtended(expr, result);
            result.add(getOperatorByTokenName(","));
            operand.setMetadata(tok, OperandPosition.CENTER);
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
            case UnaryMinusOp op -> "UMINUS";
            case UnaryPlusOp op -> "UPLUS";
            case PostfixIncrementOp op -> "++";
            case PrefixIncrementOp op -> "++U";
            case PostfixDecrementOp op -> "--";
            case PrefixDecrementOp op -> "--U";
            case PointerPackOp op -> "POINTER_&";
            case PointerUnpackOp op -> "POINTER_*";
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
            op = tokenizeExtended(unaryOp.getArgument(), result);
            token = getOperatorByTokenName(operator);
            result.add(token);
            op.setMetadata(token, OperandPosition.LEFT);
        } else {
            token = getOperatorByTokenName(operator);
            result.add(token);
            op = tokenizeExtended(unaryOp.getArgument(), result);
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
            case ThreeWayComparisonOp op -> "<=>";
            default -> null;
        };
        if (operator == null) {
            String s = viewer.toString(binOp);
            result.addAll(tokenize(s));
            return;
        }
        TokenGroup left = tokenizeExtended(binOp.getLeft(), result);
        OperatorToken token = getOperatorByTokenName(operator);
        result.add(token);
        TokenGroup right = tokenizeExtended(binOp.getRight(), result);
        left.setMetadata(token, OperandPosition.LEFT);
        right.setMetadata(token, OperandPosition.RIGHT);
        if (binOp.getAssignedValueTag() != null) {
            token.assignValue(binOp.getAssignedValueTag());
            valueSetNodes.add(binOp.getId());
        }
    }

    private void tokenizeFieldOp(MemberAccess access, TokenList result) {
        TokenGroup base = tokenizeExtended(access.getExpression(), result);
        OperatorToken dot;
        if (access instanceof PointerMemberAccess) {
            dot = getOperatorByTokenName("->");
        } else {
            dot = getOperatorByTokenName(".");
        }
        result.add(dot);
        TokenGroup member = tokenizeExtended(access.getMember(), result);
        base.setMetadata(dot, OperandPosition.LEFT);
        member.setMetadata(dot, OperandPosition.RIGHT);
        if (access.getAssignedValueTag() != null) {
            dot.assignValue(access.getAssignedValueTag());
            valueSetNodes.add(access.getId());
        }
    }

    private void tokenizeTernary(TernaryOperator ternary, TokenList result) {
        TokenGroup cond = tokenizeExtended(ternary.getCondition(), result);
        OperatorToken op1 = getOperatorByTokenName("?");
        cond.setMetadata(op1, OperandPosition.LEFT);
        result.add(op1);
        TokenGroup alt1 = tokenizeExtended(ternary.getThenExpr(), result);
        alt1.setMetadata(op1, OperandPosition.CENTER);
        OperatorToken op2 = getOperatorByTokenName(":");
        result.add(op2);
        TokenGroup alt2 = tokenizeExtended(ternary.getElseExpr(), result);
        alt2.setMetadata(op1, OperandPosition.RIGHT);
        if (ternary.getAssignedValueTag() != null) {
            op1.assignValue(ternary.getAssignedValueTag());
            valueSetNodes.add(ternary.getId());
        }
    }
}
