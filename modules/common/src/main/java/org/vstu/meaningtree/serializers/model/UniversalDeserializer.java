package org.vstu.meaningtree.serializers.model;

import org.vstu.meaningtree.exceptions.MeaningTreeException;
import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Node;
import org.vstu.meaningtree.nodes.ProgramEntryPoint;
import org.vstu.meaningtree.nodes.enums.AugmentedAssignmentOperator;
import org.vstu.meaningtree.nodes.expressions.BinaryExpression;
import org.vstu.meaningtree.nodes.expressions.Identifier;
import org.vstu.meaningtree.nodes.expressions.ParenthesizedExpression;
import org.vstu.meaningtree.nodes.expressions.UnaryExpression;
import org.vstu.meaningtree.nodes.expressions.calls.FunctionCall;
import org.vstu.meaningtree.nodes.expressions.calls.MethodCall;
import org.vstu.meaningtree.nodes.expressions.comparison.BinaryComparison;
import org.vstu.meaningtree.nodes.expressions.comparison.CompoundComparison;
import org.vstu.meaningtree.nodes.expressions.identifiers.QualifiedIdentifier;
import org.vstu.meaningtree.nodes.expressions.identifiers.ScopedIdentifier;
import org.vstu.meaningtree.nodes.expressions.identifiers.SimpleIdentifier;
import org.vstu.meaningtree.nodes.expressions.literals.BoolLiteral;
import org.vstu.meaningtree.nodes.expressions.literals.FloatLiteral;
import org.vstu.meaningtree.nodes.expressions.literals.IntegerLiteral;
import org.vstu.meaningtree.nodes.expressions.literals.StringLiteral;
import org.vstu.meaningtree.nodes.expressions.other.ExpressionSequence;
import org.vstu.meaningtree.nodes.expressions.other.IndexExpression;
import org.vstu.meaningtree.nodes.expressions.other.MemberAccess;
import org.vstu.meaningtree.nodes.expressions.other.TernaryOperator;
import org.vstu.meaningtree.nodes.io.InputCommand;
import org.vstu.meaningtree.nodes.io.PrintValues;
import org.vstu.meaningtree.nodes.statements.ExpressionStatement;
import org.vstu.meaningtree.nodes.statements.assignments.AssignmentStatement;
import org.vstu.meaningtree.utils.env.SymbolEnvironment;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class UniversalDeserializer implements Deserializer<AbstractSerializedNode> {
    public Node deserialize(AbstractSerializedNode abstractSerialized) {
        SerializedNode serialized = (SerializedNode) abstractSerialized;
        Node node = switch (serialized.nodeName) {
            case "ProgramEntryPoint" -> deserializeEntryPoint(serialized);
            case "ParenthesizedExpression" -> deserializeParen(serialized);
            case "TernaryOperator" -> deserializeTernary(serialized);
            case "SimpleIdentifier", "QualifiedIdentifier", "ScopedIdentifier" -> deserializeIdentifier(serialized);
            case "MethodCall" -> deserializeMethodCall(serialized);
            case "FunctionCall" -> deserializeFunctionCall(serialized);
            case "Integer", "Float", "String", "Boolean" -> deserializeLiteral(serialized);
            case "IndexExpression" -> deserializeIndex(serialized);
            case "CompoundComparison" -> deserializeCompound(serialized);
            case "AssignmentStatement" -> deserializeAssignmentStmt(serialized);
            case "AssignmentExpression" -> deserializeAssignmentExpr(serialized);
            case "ExpressionSequence" -> deserializeExprSequence(serialized);
            case "ExpressionStatement" -> deserializeExprStmt(serialized);
            case "MemberAccess", "PointerMemberAccess" -> deserializeMemberAccess(serialized);
            default -> deserializeOther(serialized);
        };
        if (abstractSerialized.values.containsKey("assignedValueTag")) {
            node.setAssignedValueTag(abstractSerialized.values.get("assignedValueTag"));
        }
        return node;
    }

    private Node deserializeEntryPoint(SerializedNode serialized) {
        return new ProgramEntryPoint(new SymbolEnvironment(null), (List<Node>) deserializeList(
                (SerializedListNode) serialized.fields.get("body"))
        );
    }

    private Node deserializeMemberAccess(SerializedNode serialized) {
        return new MemberAccess(
                (Expression) deserialize(serialized.fields.get("owner")),
                (SimpleIdentifier) deserialize(serialized.fields.get("member"))
        );
    }

    private Node deserializeExprStmt(SerializedNode serialized) {
        return new ExpressionStatement((Expression) deserialize(serialized.fields.get("expr")));
    }

    private Node deserializeExprSequence(SerializedNode serialized) {
        return new ExpressionSequence((List<Expression>) deserializeList((SerializedListNode) serialized.fields.get("exprs")));
    }

    private Node deserializeAssignmentExpr(SerializedNode serialized) {
        return new AssignmentStatement(
                (Expression) deserialize(serialized.fields.get("left")),
                (Expression) deserialize(serialized.fields.get("right")),
                AugmentedAssignmentOperator.valueOf((String) serialized.values.get("augmentedOp"))
        );
    }

    private Node deserializeAssignmentStmt(SerializedNode serialized) {
        return new AssignmentStatement(
                (Expression) deserialize(serialized.fields.get("left")),
                (Expression) deserialize(serialized.fields.get("right")),
                AugmentedAssignmentOperator.valueOf((String) serialized.values.get("augmentedOp"))
                );
    }

    private Node deserializeCompound(SerializedNode serialized) {
        return new CompoundComparison((List<BinaryComparison>) deserializeList((SerializedListNode) serialized.fields.get("members")));
    }

    private Node deserializeIndex(SerializedNode serialized) {
        return new IndexExpression((Expression) deserialize(serialized.fields.get("expr")),
                (Expression) deserialize(serialized.fields.get("index"))
                );
    }

    private Node deserializeLiteral(SerializedNode serialized) {
        return switch (serialized.nodeName) {
            case "Integer" -> new IntegerLiteral((String) serialized.values.get("text"));
            case "Float" -> new FloatLiteral((String) serialized.values.get("text"));
            case "String" -> StringLiteral.fromEscaped((String) serialized.values.get("text"), StringLiteral.Type.NONE);
            case "Boolean" -> new BoolLiteral((boolean) serialized.values.get("text"));
            default -> throw new MeaningTreeException("Unsupported literal in universal deserializer");
        };
    }

    private Node deserializeMethodCall(SerializedNode serialized) {
        return new MethodCall(
                (Expression) deserialize(serialized.fields.get("object")),
                (Identifier) deserialize(serialized.fields.get("name")),
                (List<Expression>) deserializeList((SerializedListNode) serialized.fields.get("args"))
                );
    }

    private Node deserializeFunctionCall(SerializedNode serialized) {
        if (serialized.values.containsKey("spec")) {
            String className = (String) serialized.values.get("spec");
            switch (className) {
                case "PrintValues" -> {

                    return new PrintValues(
                            (List<Expression>) deserializeList((SerializedListNode) serialized.fields.get("args")),
                            serialized.fields.containsKey("separator") ? (StringLiteral) deserialize(serialized.fields.get("separator")) : null,
                            serialized.fields.containsKey("end") ? (StringLiteral) deserialize(serialized.fields.get("end")) : null
                    );
                }
                case "InputCommand" -> {
                    return new InputCommand((List<Expression>) deserializeList((SerializedListNode) serialized.fields.get("args")));
                }
            }
        }
        return new FunctionCall(
                (Identifier) deserialize(serialized.fields.get("name")),
                (List<Expression>) deserializeList((SerializedListNode) serialized.fields.get("args"))
        );
    }

    private Node deserializeIdentifier(SerializedNode serialized) {
        return switch(serialized.nodeName) {
            case "QualifiedIdentifier" -> new QualifiedIdentifier(
                    (Identifier) deserialize(serialized.fields.get("scope")),
                    (SimpleIdentifier) deserialize(serialized.fields.get("member"))
                    );
            case "SimpleIdentifier" -> new SimpleIdentifier((String) serialized.values.get("text"));
            case "ScopedIdentifier" -> new ScopedIdentifier((List<SimpleIdentifier>) deserializeList((SerializedListNode) serialized.fields.get("members")));
            default -> throw new MeaningTreeException("Unknown identifier in universal deserializer");
        };
    }

    private Node deserializeTernary(SerializedNode serialized) {
        return new TernaryOperator(
                (Expression) deserialize(serialized.fields.get("condition")),
                (Expression) deserialize(serialized.fields.get("then")),
                (Expression) deserialize(serialized.fields.get("else"))
        );
    }

    private Node deserializeParen(SerializedNode serialized) {
        return new ParenthesizedExpression((Expression) deserialize(serialized.fields.get("value")));
    }

    private Node deserializeOther(SerializedNode serialized) {
        Class clazz = null;
        try {
            clazz = Class.forName(
                    serialized.nodeName.startsWith("org.vstu.meaningtree.nodes.") ? serialized.nodeName :
                    "org.vstu.meaningtree.nodes.".concat(serialized.nodeName)
            );
        } catch (ClassNotFoundException e) {
            throw new MeaningTreeException("Unsupported serialized node in universal deserializer");
        }
        if (BinaryExpression.class.isAssignableFrom(clazz)) {
            try {
                return (Node) clazz.getDeclaredConstructor(Expression.class, Expression.class).newInstance(
                        deserialize(serialized.fields.get("left")),
                        deserialize(serialized.fields.get("right"))
                        );
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
            }
        }

        if (UnaryExpression.class.isAssignableFrom(clazz)) {
            try {
                return (Node) clazz.getDeclaredConstructor(Expression.class).newInstance(
                        deserialize(serialized.fields.get("arg"))
                );
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
            }
        }

        throw new MeaningTreeException("Unsupported serialized node in universal deserializer");
    }

    private List<? extends Node> deserializeList(SerializedListNode serializedListNode) {
        return serializedListNode.nodes.stream().map(this::deserialize).toList();
    }
}
