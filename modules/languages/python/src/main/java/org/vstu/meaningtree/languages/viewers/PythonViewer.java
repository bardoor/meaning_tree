package org.vstu.meaningtree.languages.viewers;

import org.vstu.meaningtree.languages.PythonSpecialTreeTransformations;
import org.vstu.meaningtree.languages.utils.Tab;
import org.vstu.meaningtree.nodes.*;
import org.vstu.meaningtree.nodes.bitwise.*;
import org.vstu.meaningtree.nodes.comparison.*;
import org.vstu.meaningtree.nodes.declarations.*;
import org.vstu.meaningtree.nodes.definitions.FunctionDefinition;
import org.vstu.meaningtree.nodes.definitions.MethodDefinition;
import org.vstu.meaningtree.nodes.identifiers.ScopedIdentifier;
import org.vstu.meaningtree.nodes.identifiers.SimpleIdentifier;
import org.vstu.meaningtree.nodes.literals.*;
import org.vstu.meaningtree.nodes.logical.NotOp;
import org.vstu.meaningtree.nodes.logical.ShortCircuitAndOp;
import org.vstu.meaningtree.nodes.logical.ShortCircuitOrOp;
import org.vstu.meaningtree.nodes.math.*;
import org.vstu.meaningtree.nodes.statements.*;
import org.vstu.meaningtree.nodes.unary.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedMap;


public class PythonViewer extends Viewer {
    private record TaggedBinaryComparisonOperand(Expression wrapped, boolean hasEqual) { }
    /*
    TODO:
     - general for-loop/while transformation
     - function support
     - class support
     - import support
     */

    @Override
    public String toString(Node node) {
        Tab tab = new Tab();
        return toString(node, tab);
    }

    public String toString(Node node, Tab tab) {
        return switch (node) {
            case ProgramEntryPoint programEntryPoint -> entryPointToString(programEntryPoint, tab);
            case BinaryComparison cmpNode -> comparisonToString(cmpNode);
            case BinaryExpression binaryExpression -> binaryOpToString(binaryExpression);
            case IfStatement ifStatement -> conditionToString(ifStatement, tab);
            case UnaryExpression exprNode -> unaryToString(exprNode);
            case CompoundStatement exprNode -> blockToString(exprNode, tab);
            case CompoundComparison compound -> compoundComparisonToString(compound);
            case Identifier identifier -> identifier.toString();
            case IndexExpression indexExpr -> String.format("%s[%s]", toString(indexExpr.getExpr()), toString(indexExpr.getIndex()));
            case MemberAccess memAccess -> String.format("%s.%s", toString(memAccess.getExpression()), toString(memAccess.getMember()));
            case TernaryOperator ternary -> String.format("%s ? %s : %s", toString(ternary.getCondition()), toString(ternary.getThenExpr()), toString(ternary.getElseExpr()));
            case ParenthesizedExpression paren -> String.format("(%s)", toString(paren.getExpression()));
            case ObjectNewExpression newExpr -> callsToString(newExpr);
            case ArrayNewExpression newExpr -> callsToString(newExpr);
            case FunctionCall funcCall -> callsToString(funcCall);
            case BreakStatement breakStmt -> "break";
            case Range range -> rangeToString(range);
            case ContinueStatement continueStatement -> "continue";
            case Comment comment -> commentToString(comment);
            case Literal literal -> literalToString(literal);
            case AssignmentExpression assignmentExpr -> assignmentExpressionToString(assignmentExpr);
            case AssignmentStatement assignmentStatement -> assignmentToString(assignmentStatement);
            case VariableDeclaration varDecl -> variableDeclarationToString(varDecl);
            case ForLoop forLoop -> loopToString(forLoop, tab);
            case WhileLoop whileLoop -> loopToString(whileLoop, tab);
            case DoWhileLoop doWhileLoop -> loopToString(doWhileLoop, tab);
            case SwitchStatement switchStmt -> loopToString(switchStmt, tab);
            case null, default -> throw new RuntimeException("Unsupported tree element");
        };
    }

    private String entryPointToString(ProgramEntryPoint programEntryPoint, Tab tab) {
        IfStatement entryPointIf = null;
        if (programEntryPoint.hasEntryPoint()) {
            Node entryPointNode = programEntryPoint.getEntryPoint();
            if (entryPointNode instanceof FunctionDefinition func) {
                Identifier ident;
                FunctionDeclaration funcDecl = (FunctionDeclaration) func.getDeclaration();
                if (funcDecl instanceof MethodDeclaration method) {
                    ident = new ScopedIdentifier(method.getOwner().getName(), method.getName());
                } else {
                    ident = func.getName();
                }
                //NOTE: default behaviour - ignore arguments in call main function
                List<Expression> nulls = new ArrayList<>();
                for (DeclarationArgument arg : funcDecl.getArguments()) {
                    if (!arg.isListUnpacking()) {
                        nulls.add(new NullLiteral());
                    }
                }
                FunctionCall funcCall = new FunctionCall(ident, nulls.toArray(new Expression[0]));
                entryPointIf = new IfStatement(new EqOp(new SimpleIdentifier("__name__"), new StringLiteral("__main__")), new CompoundStatement(funcCall),null);
            } else if (entryPointNode instanceof CompoundStatement compound) {
                entryPointIf = new IfStatement(new EqOp(new SimpleIdentifier("__name__"), new StringLiteral("__main__")), compound,null);
            }
        }
        List<Node> nodes = new ArrayList<>(programEntryPoint.getBody());
        nodes.add(entryPointIf);
        return nodeListToString(nodes, tab);
    }

    private String loopToString(Statement stmt, Tab tab) {
        StringBuilder builder = new StringBuilder();
        if (stmt instanceof RangeForLoop rangeFor) {
            builder.append(String.format("for %s in range(%s, %s, %s):\n",
                    toString(rangeFor.getIdentifier()),
                    toString(rangeFor.getStart()),
                    toString(rangeFor.getEnd()),
                    toString(rangeFor.getStep())
            ));
            builder.append(toString(rangeFor.getBody(), tab));
        } else if (stmt instanceof GeneralForLoop generalFor) {
            return toString(PythonSpecialTreeTransformations.representGeneralFor(generalFor));
        } else if (stmt instanceof DoWhileLoop doWhile) {
            return toString(PythonSpecialTreeTransformations.representDoWhile(doWhile));
        } else if (stmt instanceof WhileLoop whileLoop) {
            builder.append(String.format("while %s:\n", toString(whileLoop.getCondition())));
            builder.append(toString(whileLoop.getBody(), tab));
        } else if (stmt instanceof ForEachLoop forEachLoop) {
            List<Expression> identifiers = new ArrayList<>();
            for (VariableDeclarator decl : forEachLoop.getItem().getDeclarators()) {
                identifiers.add(decl.getIdentifier());
            }
            builder.append(String.format("for %s in %s:\n", argumentsToString(identifiers), toString(forEachLoop.getExpression())));
            builder.append(toString(forEachLoop.getBody(), tab));
        } else if (stmt instanceof SwitchStatement switchStmt) {
            tab = tab.up();
            builder.append(String.format("match %s:\n", switchStmt.getTargetExpression()));
            for (ConditionBranch caseBranch : switchStmt.getCases()) {
                builder.append(String.format("%scase %s:\n%s\n\n", tab, toString(caseBranch.getCondition()), toString(caseBranch.getBody(), tab)));
            }
            if (switchStmt.hasDefaultCase()) {
                builder.append(String.format("%scase _:\n%s\n", tab, toString(switchStmt.getDefaultCase(), tab)));
            }
        }
        return builder.toString();
    }

    private String variableDeclarationToString(VariableDeclaration varDecl) {
        StringBuilder lValues = new StringBuilder();
        StringBuilder rValues = new StringBuilder();
        VariableDeclarator[] decls = varDecl.getDeclarators();
        for (int i = 0; i < decls.length; i++) {
            lValues.append(toString(decls[i].getIdentifier()));
            if (decls[i].hasInitialization()) {
                rValues.append(toString(decls[i].getRValue()));
            } else {
                rValues.append("None");
            }
            if (i != decls.length - 1) {
                lValues.append(", ");
                rValues.append(", ");
            }
        }
        return String.format("%s = %s", lValues, rValues);
    }

    private String assignmentExpressionToString(AssignmentExpression expr) {
        return String.format("%s := %s", toString(expr.getLValue()), toString(expr.getRValue()));
    }

    private String assignmentToString(AssignmentStatement stmt) {
        return String.format("%s = %s", toString(stmt.getLValue()), toString(stmt.getRValue()));
    }

    private String literalToString(Literal literal) {
        if (literal instanceof NumericLiteral numLiteral) {
            //TODO: What about various numeric representation like 0xABC, 0b010101, 0o334?
            return numLiteral.getValue().toString();
        } else if (literal instanceof StringLiteral strLiteral) {
            //TODO: What about f-string, escaped characters, raw strings?
            return String.format("\"%s\"", strLiteral.getValue());
        } else if (literal instanceof ListLiteral list) {
            return String.format("[%s]", argumentsToString(list.getList()));
        } else if (literal instanceof SetLiteral set) {
            return String.format("{%s}", argumentsToString(set.getList()));
        } else if (literal instanceof UnmodifiableListLiteral tuple) {
            return String.format("(%s)", argumentsToString(tuple.getList()));
        } else if (literal instanceof DictionaryLiteral dict) {
            SortedMap<Expression, Expression> map = dict.getDictionary();
            StringBuilder builder = new StringBuilder();
            builder.append('{');
            for (Expression key : map.keySet()) {
                builder.append(String.format("%s : %s,", key, map.get(key)));
            }
            builder.append("}", builder.length() - 1, builder.length());
            return builder.toString();
        } else {
            return "None";
        }
    }

    private String commentToString(Comment comment) {
        if (comment.isMultiline()) {
            return String.format("\"\"\"\n%s\n\"\"\"", comment.getContent());
        } else {
            return String.format("# %s", comment.getContent());
        }
    }

    private String rangeToString(Range range) {
        StringBuilder builder = new StringBuilder();
        if (range.hasStart()) {
            builder.append(toString(range.getStart()));
        }
        builder.append(':');
        if (range.hasStop()) {
            builder.append(toString(range.getStop()));
        }
        if (range.hasStep()) {
            builder.append(':');
            builder.append(toString(range.getStep()));
        }
        return builder.toString();
    }

    private String binaryOpToString(BinaryExpression node) {
        String pattern = "";
        if (node instanceof AddOp) {
            pattern = "%s + %s";
        } else if (node instanceof SubOp) {
            pattern = "%s - %s";
        } else if (node instanceof MulOp) {
            pattern = "%s * %s";
        } else if (node instanceof DivOp) {
            pattern = "%s / %s";
        } else if (node instanceof PowOp) {
            pattern = "%s ** %s";
        } else if (node instanceof FloorDivOp) {
            pattern = "%s // %s";
        } else if (node instanceof ModOp) {
            pattern = "%s %% %s";
        } else if (node instanceof BitwiseAndOp) {
            pattern = "%s & %s";
        } else if (node instanceof BitwiseOrOp) {
            pattern = "%s | %s";
        } else if (node instanceof RightShiftOp) {
            pattern = "%s >> %s";
        } else if (node instanceof LeftShiftOp) {
            pattern = "%s << %s";
        } else if (node instanceof XorOp) {
            pattern = "%s ^ %s";
        } else if (node instanceof ShortCircuitAndOp) {
            Node result = detectCompoundComparison(node);
            if (result instanceof CompoundComparison) {
                return compoundComparisonToString((CompoundComparison) result);
            } else if (result instanceof ShortCircuitAndOp) {
                pattern = "%s and %s";
            } else {
                return toString(node);
            }
        } else if (node instanceof ShortCircuitOrOp) {
            pattern = "%s or %s";
        }
        return String.format(pattern, toString(node.getLeft()), toString(node.getRight()));
    }

    private String callsToString(Node node) {
        if (node instanceof ArrayNewExpression newExpr) {
            if (!newExpr.getInitialArray().isEmpty()) {
                return String.format("[%s]", argumentsToString(newExpr.getInitialArray()));
            } else {
                return String.format("[%s for _ in range(%s)]", String.format("%s()", newExpr.getType()), toString(newExpr.getDimension()));
            }
        } else if (node instanceof ObjectNewExpression newExpr) {
            return String.format("%s(%s)", newExpr.getType(), argumentsToString(newExpr.getConstructorArguments()));
        } else if (node instanceof FunctionCall funcCall) {
            return String.format("%s(%s)", funcCall.getFunctionName(), argumentsToString(funcCall.getArguments()));
        } else {
            throw new RuntimeException("Not a callable object");
        }
    }

    private String argumentsToString(List<Expression> expressions) {
        String[] exprStrings = new String[expressions.size()];
        for (int i = 0; i < exprStrings.length; i++) {
            exprStrings[i] = toString(expressions.get(i));
        }
        return String.join(", ", exprStrings);
    }

    private String conditionToString(IfStatement node, Tab tab) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < node.getBranches().size(); i++) {
            String pattern = "elif %s:\n%s\n";
            if (i == 0) {
                pattern = "if %s:\n%s\n";
            }
            ConditionBranch branch = node.getBranches().get(i);
            sb.append(String.format(pattern, branch.getCondition(), toString(branch.getBody(), tab)));
        }
        sb.append(String.format("else:%s\n", toString(node.getElseBranch(), tab)));
        return sb.toString();
    }

    private String unaryToString(UnaryExpression node) {
        String pattern = "";
        if (node instanceof UnaryPlusOp) {
            pattern = "+%s";
        } else if (node instanceof UnaryMinusOp) {
            pattern = "-%s";
        } else if (node instanceof NotOp) {
            pattern = "not %s";
        } else if (node instanceof InversionOp) {
            pattern = "~%s";
        } else if (node instanceof PostfixDecrementOp || node instanceof PrefixDecrementOp) {
            pattern = "%s -= 1";
        } else if (node instanceof PostfixIncrementOp || node instanceof PrefixIncrementOp) {
            pattern = "%s += 1";
        }
        return String.format(pattern, toString(node.getArgument()));
    }

    private String blockToString(CompoundStatement node, Tab tab) {
        StringBuilder builder = new StringBuilder();
        tab = tab.up();
        for (Node child : node) {
            builder.append(tab);
            builder.append(toString(node, tab));
            builder.append('\n');
        }
        return builder.toString();
    }

    private String nodeListToString(List<Node> nodes, Tab tab) {
        StringBuilder builder = new StringBuilder();
        for (Node child : nodes) {
            builder.append(tab);
            builder.append(toString(child, tab));
            builder.append('\n');
        }
        return builder.toString();
    }

    // COMPARISONS SECTION //
    //TODO: very unstable code, needed more tests
    private Node detectCompoundComparison(Node expressionNode) {
        if (expressionNode instanceof ShortCircuitAndOp op) {
            Expression left = op.getLeft();
            Expression right = op.getRight();

            ArrayList<Expression> secondary = new ArrayList<>();
            ArrayList<BinaryComparison> primary = new ArrayList<>();
            _collectCompoundStatementElements(left, primary, secondary);
            _collectCompoundStatementElements(right, primary, secondary);
            ArrayList<BinaryComparison> primaryCopy = new ArrayList<>(primary);
            TaggedBinaryComparisonOperand[] expressions = new TaggedBinaryComparisonOperand[2 * primary.size() + 1];
            for (int i = 1; i < expressions.length - 1 && !primary.isEmpty(); i+=3) {
                BinaryComparison expr = primary.removeFirst();
                Expression leftOperand = expr.getLeft();
                Expression rightOperand = expr.getRight();
                int leftOperandFoundWeight = _findSameExpression(leftOperand, expressions);
                int rightOperandFoundWeight = _findSameExpression(rightOperand, expressions);

                int leftWeight, rightWeight;
                if (expr instanceof LtOp || expr instanceof LeOp) {
                    leftWeight = leftOperandFoundWeight != -1 ? leftOperandFoundWeight : i;
                    rightWeight = rightOperandFoundWeight != -1 ? rightOperandFoundWeight : i + 1;
                } else {
                    leftWeight = leftOperandFoundWeight != -1 ? leftOperandFoundWeight : i + 1;
                    rightWeight = rightOperandFoundWeight != -1 ? rightOperandFoundWeight : i;
                }

                if (expr instanceof LtOp || expr instanceof LeOp) {
                    if (leftWeight < rightWeight) {
                        expressions[leftWeight] = new TaggedBinaryComparisonOperand(leftOperand, false);
                        expressions[rightWeight] = new TaggedBinaryComparisonOperand(rightOperand, expr instanceof LeOp);
                    } else {
                        expressions[leftWeight] = new TaggedBinaryComparisonOperand(rightOperand, false);
                        expressions[rightWeight] = new TaggedBinaryComparisonOperand(leftOperand, expr instanceof LeOp);
                    }
                } else if (expr instanceof GeOp || expr instanceof GtOp) {
                    if (leftWeight > rightWeight) {
                        expressions[leftWeight] = new TaggedBinaryComparisonOperand(leftOperand, false);
                        expressions[rightWeight] = new TaggedBinaryComparisonOperand(rightOperand, expr instanceof GeOp);
                    } else {
                        expressions[leftWeight] = new TaggedBinaryComparisonOperand(rightOperand, false);
                        expressions[rightWeight] = new TaggedBinaryComparisonOperand(leftOperand, expr instanceof GeOp);
                    }
                }
            }
            for (BinaryComparison cmp : primaryCopy) {
                Expression leftOperand = cmp.getLeft();
                Expression rightOperand = cmp.getRight();

                int leftWeight = _findSameExpression(leftOperand, expressions);
                int rightWeight = _findSameExpression(rightOperand, expressions);

                if (Math.abs(leftWeight - rightWeight) == 1) {
                    continue;
                }

                if (leftWeight > rightWeight) {
                    int tmp = leftWeight;
                    leftWeight = rightWeight;
                    rightWeight = tmp;
                }

                int leftA, leftB, rightA, rightB;
                for (leftA = leftWeight; leftA > 0 && expressions[leftA] != null; leftA--);
                for (leftB = leftWeight; leftB < expressions.length && expressions[leftB] != null; leftB++);

                for (rightA = rightWeight; rightA > 0 && expressions[rightA] != null; rightA--);
                for (rightB = rightWeight; rightB < expressions.length && expressions[rightB] != null; rightB++);

                rightA++;
                rightB--;
                leftA++;
                rightB--;

                boolean isEnoughPlace = true;
                for (int i = leftB + 1; i < leftB + (rightB - rightA + 1); i++) {
                    isEnoughPlace &= expressions[i] == null;
                }

                if (isEnoughPlace) {
                    for (int i = leftB + 1; i < leftB + (rightB - rightA + 1); i++) {
                        expressions[i] = expressions[rightA];
                        expressions[rightA] = null;
                        rightA++;
                    }
                    assert rightA == rightB + 1;
                }
            }
            List<BinaryComparison> tempComparisons = new ArrayList<>();
            List<CompoundComparison> compounds = new ArrayList<>();
            for (int i = 0; i < expressions.length - 1; i++) {
                if (expressions[i] == null || expressions[i + 1] == null) {
                    if (!tempComparisons.isEmpty()) {
                        compounds.add(new CompoundComparison(tempComparisons.toArray(new BinaryComparison[0])));
                    }
                } else {
                    if (expressions[i + 1].hasEqual) {
                        tempComparisons.add(new LeOp(expressions[i].wrapped, expressions[i + 1].wrapped));
                    } else {
                        tempComparisons.add(new LtOp(expressions[i].wrapped, expressions[i + 1].wrapped));
                    }
                }
            }
            if (compounds.size() == 1 && secondary.isEmpty()) {
                return compounds.getFirst();
            }
            Expression primaryAndOp = BinaryExpression.fromManyOperands(compounds.toArray(new Expression[0]), 0, ShortCircuitAndOp.class);
            Expression secondaryAndOp = BinaryExpression.fromManyOperands(secondary.toArray(new Expression[0]), 0, ShortCircuitAndOp.class);
            return new ShortCircuitAndOp(primaryAndOp, secondaryAndOp);
        }
        return expressionNode;
    }

    private int _findSameExpression(Expression expr, TaggedBinaryComparisonOperand[] weighted) {
        for (int i = 0; i < weighted.length; i++) {
            if (expr.equals(weighted[i].wrapped)) {
                return i;
            }
        }
        return -1;
    }

    private boolean _collectCompoundStatementElements(Expression node, List<BinaryComparison> primary, List<Expression> secondary) {
        if (node instanceof BinaryComparison op) {
            if (op instanceof NotEqOp || op instanceof EqOp) {
                secondary.add(op);
            } else {
                primary.add(op);
            }
        } else if (node instanceof ShortCircuitAndOp op) {
            boolean result = _collectCompoundStatementElements(node, primary, secondary);
            if (!result) {
                secondary.add(op);
                return false;
            }
            return result;
        } else {
            secondary.add(node);
        }
        return false;
    }

    private String comparisonToString(BinaryComparison node) {
        String pattern = "";
        if (node instanceof EqOp) {
            pattern = "%s == %s";
        } else if (node instanceof NotEqOp) {
            pattern = "%s != %s";
        } else if (node instanceof GeOp) {
            pattern = "%s >= %s";
        } else if (node instanceof LeOp) {
            pattern = "%s <= %s";
        } else if (node instanceof GtOp) {
            pattern = "%s > %s";
        } else if (node instanceof LtOp) {
            pattern = "%s < %s";
        }
        return String.format(pattern, toString(node.getLeft()), toString(node.getRight()));
    }

    private String compoundComparisonToString(CompoundComparison node) {
        StringBuilder sb = new StringBuilder();
        sb.append(toString(node.getComparisons().getFirst().getLeft()));
        for (BinaryComparison cmp : node.getComparisons()) {
            if (cmp instanceof EqOp) {
                sb.append(" == ");
            } else if (cmp instanceof NotEqOp) {
                sb.append(" != ");
            } else if (cmp instanceof GeOp) {
                sb.append(" >= ");
            } else if (cmp instanceof LeOp) {
                sb.append(" <= ");
            } else if (cmp instanceof GtOp) {
                sb.append(" > ");
            } else if (cmp instanceof LtOp) {
                sb.append(" < ");
            }
            sb.append(toString(cmp.getRight()));
        }
        return sb.toString();
    }

    // END COMPARISONS SECTION //
}