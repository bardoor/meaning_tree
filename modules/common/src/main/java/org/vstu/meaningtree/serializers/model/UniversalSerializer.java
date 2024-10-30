package org.vstu.meaningtree.serializers.model;

import org.vstu.meaningtree.exceptions.MeaningTreeException;
import org.vstu.meaningtree.nodes.Node;
import org.vstu.meaningtree.nodes.expressions.*;
import org.vstu.meaningtree.nodes.expressions.calls.FunctionCall;
import org.vstu.meaningtree.nodes.expressions.calls.MethodCall;
import org.vstu.meaningtree.nodes.expressions.comparison.CompoundComparison;
import org.vstu.meaningtree.nodes.expressions.identifiers.QualifiedIdentifier;
import org.vstu.meaningtree.nodes.expressions.identifiers.ScopedIdentifier;
import org.vstu.meaningtree.nodes.expressions.identifiers.SimpleIdentifier;
import org.vstu.meaningtree.nodes.expressions.literals.BoolLiteral;
import org.vstu.meaningtree.nodes.expressions.literals.FloatLiteral;
import org.vstu.meaningtree.nodes.expressions.literals.IntegerLiteral;
import org.vstu.meaningtree.nodes.expressions.literals.StringLiteral;
import org.vstu.meaningtree.nodes.expressions.other.AssignmentExpression;
import org.vstu.meaningtree.nodes.expressions.other.IndexExpression;
import org.vstu.meaningtree.nodes.expressions.other.MemberAccess;
import org.vstu.meaningtree.nodes.expressions.other.TernaryOperator;
import org.vstu.meaningtree.nodes.statements.ExpressionSequence;
import org.vstu.meaningtree.nodes.statements.ExpressionStatement;
import org.vstu.meaningtree.nodes.statements.assignments.AssignmentStatement;

import java.util.*;

public class UniversalSerializer implements Serializer<AbstractSerializedNode> {
    @Override
    public SerializedNode serialize(Node node) {
        SerializedNode result =  switch (node) {
            case BinaryExpression expr -> serialize(expr);
            case UnaryExpression expr -> serialize(expr);
            case ParenthesizedExpression expr -> serialize(expr);
            case TernaryOperator ternary -> serialize(ternary);
            case Identifier ident -> serialize(ident);
            case MethodCall call -> serialize(call);
            case FunctionCall call -> serialize(call);
            case Literal literal -> serialize(literal);
            case IndexExpression expr -> serialize(expr);
            case CompoundComparison compound -> serialize(compound);
            case AssignmentStatement stmt -> serialize(stmt);
            case AssignmentExpression expr -> serialize(expr);
            case ExpressionSequence sequence -> serialize(sequence);
            case ExpressionStatement stmt -> serialize(stmt);
            case MemberAccess member -> serialize(member);
            default -> serializeDefault(node);
        };
        if (node.getAssignedValueTag() != null) {
            result.values.put("assignedValueTag", node.getAssignedValueTag());
        }
        return result;
    }

    public SerializedListNode serialize(List<? extends Node> nodes) {
        return new SerializedListNode(nodes.stream().map((Node node) -> serialize(node)).toList());
    }

    public SerializedListNode serialize(Node[] nodes) {
        return new SerializedListNode(Arrays.stream(nodes).map(this::serialize).toList());
    }

    public SerializedNode serialize(BinaryExpression binOp) {
        return new SerializedNode(binOp.getNodeUniqueName(),
                new HashMap<>() {{
                    put("left", serialize(binOp.getLeft()));
                    put("right", serialize(binOp.getRight()));
                }}
                );
    }

    public SerializedNode serialize(TernaryOperator ternaryOp) {
        return new SerializedNode("TernaryOperator",
                new HashMap<>() {{
                    put("condition", serialize(ternaryOp.getCondition()));
                    put("then", serialize(ternaryOp.getThenExpr()));
                    put("else", serialize(ternaryOp.getElseExpr()));
                }}
        );
    }

    public SerializedNode serialize(UnaryExpression unaryOp) {
        return new SerializedNode(unaryOp.getNodeUniqueName(),
                new HashMap<>() {{
                    put("arg", serialize(unaryOp.getArgument()));
                }}
        );
    }

    public SerializedNode serialize(FunctionCall call) {
        return new SerializedNode("FunctionCall", new HashMap<>() {{
            put("name", serialize(call.getFunction()));
            put("args", serialize(call.getArguments()));
        }});
    }

    public SerializedNode serialize(MethodCall call) {
        return new SerializedNode("MethodCall", new HashMap<>() {{
            put("object", serialize(call.getObject()));
            put("name", serialize(call.getFunction()));
            put("args", serialize(call.getArguments()));
        }});
    }

    public SerializedNode serialize(Identifier ident) {
        return switch (ident) {
            case SimpleIdentifier simple -> new SerializedNode("SimpleIdentifier", new HashMap<>(), new HashMap<>() {{
                put("text", simple.toString());
            }});
            case QualifiedIdentifier qualified -> new SerializedNode("QualifiedIdentifier", new HashMap<>() {{
                put("scope", serialize(qualified.getScope()));
                put("member", serialize(qualified.getMember()));
            }});
            case ScopedIdentifier scoped -> new SerializedNode("ScopedIdentifier", new HashMap<>() {{
                put("members", serialize(scoped.getScopeResolution()));
            }});
            default -> throw new MeaningTreeException("Unknown identifier in universal serializer");
        };
    }

    public SerializedNode serialize(Literal literal) {
        return switch (literal) {
            case IntegerLiteral number -> new SerializedNode("Integer", new HashMap<>(), new HashMap<>() {{
                put("text", number.getStringValue(true));
                put("value", number.getLongValue());
            }});
            case FloatLiteral number -> new SerializedNode("Float", new HashMap<>(), new HashMap<>() {{
                put("text", number.getStringValue(true));
                put("value", number.getDoubleValue());
            }});
            case StringLiteral str -> new SerializedNode("String", new HashMap<>(), new HashMap<>() {{
                put("text", str.getEscapedValue());
            }});
            case BoolLiteral bool -> new SerializedNode("Boolean", new HashMap<>(), new HashMap<>() {{
                put("text", bool.getValue());
            }});
            default -> serializeDefault(literal);
        };
    }

    private SerializedNode serializeDefault(Node node) {
        HashMap<String, AbstractSerializedNode> nodes = new HashMap<>();
        for (Map.Entry<String, Object> child : node.getChildren().entrySet()) {
            if (child.getValue() instanceof List) {
                nodes.put(child.getKey(), serialize((List<Node>) child.getValue()));
            } else if (child.getValue() instanceof Node[]) {
                nodes.put(child.getKey(), serialize((Node[]) child.getValue()));
            } else if (child.getValue() instanceof Node) {
                nodes.put(child.getKey(), serialize((Node) child.getValue()));
            } else if (child.getValue() instanceof Optional opt && opt.isPresent()) {
                nodes.put(child.getKey(), serialize((Node)opt.get()));
            }
        }
        return new SerializedNode(node.getNodeUniqueName(), nodes);
    }

    public SerializedNode serialize(ParenthesizedExpression paren) {
        return new SerializedNode("ParenthesizedExpression", new HashMap<>() {{
            put("value", serialize(paren.getExpression()));
        }});
    }

    public SerializedNode serialize(IndexExpression index) {
        return new SerializedNode("IndexExpression", new HashMap<>() {{
            put("expr", serialize(index.getExpr()));
            put("index", serialize(index.getIndex()));
        }});
    }


    public SerializedNode serialize(CompoundComparison compound) {
        return new SerializedNode("CompoundComparison", new HashMap<>() {{
            put("members", serialize(compound.getComparisons()));
        }});
    }

    public SerializedNode serialize(AssignmentExpression expr) {
        return new SerializedNode("AssignmentExpression", new HashMap<>() {{
            put("left", serialize(expr.getLValue()));
            put("right", serialize(expr.getRValue()));
        }}, new HashMap<>() {{
            put("augmentedOp", expr.getAugmentedOperator().toString());
        }});
    }

    public SerializedNode serialize(AssignmentStatement stmt) {
        return new SerializedNode("AssignmentStatement", new HashMap<>() {{
            put("left", serialize(stmt.getLValue()));
            put("right", serialize(stmt.getRValue()));
        }}, new HashMap<>() {{
            put("augmentedOp", stmt.getAugmentedOperator().toString());
        }});
    }

    public SerializedNode serialize(ExpressionStatement stmt) {
        return new SerializedNode("ExpressionStatement", new HashMap<>() {{
            put("expr", serialize(stmt.getExpression()));
        }});
    }

    public SerializedNode serialize(ExpressionSequence seq) {
        return new SerializedNode("ExpressionSequence", new HashMap<>() {{
            put("exprs", serialize(seq.getExpressions()));
        }});
    }

    public SerializedNode serialize(MemberAccess access) {
        return new SerializedNode(access.getClass().getSimpleName(), new HashMap<>() {{
            put("owner", serialize(access.getExpression()));
            put("member", serialize(access.getMember()));
        }});
    }
}
