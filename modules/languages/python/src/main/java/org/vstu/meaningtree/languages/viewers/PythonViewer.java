package org.vstu.meaningtree.languages.viewers;

import org.vstu.meaningtree.nodes.*;
import org.vstu.meaningtree.nodes.bitwise.*;
import org.vstu.meaningtree.nodes.comparison.*;
import org.vstu.meaningtree.nodes.logical.NotOp;
import org.vstu.meaningtree.nodes.logical.ShortCircuitAndOp;
import org.vstu.meaningtree.nodes.logical.ShortCircuitOrOp;
import org.vstu.meaningtree.nodes.math.*;
import org.vstu.meaningtree.nodes.statements.CompoundStatement;
import org.vstu.meaningtree.nodes.statements.IfStatement;
import org.vstu.meaningtree.nodes.unary.*;

import java.util.ArrayList;
import java.util.List;


public class PythonViewer extends Viewer {
    private record TaggedBinaryComparisonOperand(Expression wrapped, boolean hasEqual) { }
    /*
    TODO:
     - identifiers
     - parenthesize
     - range
     - literal support, comment support
     - new expressions
     - assignment support, variable declaration
     - tabulation
     - support if/for/for-each/while/ternary/do-while/switch
     - break/return/contiune
     - program entry point
     - general for-loop transformation
     - support function calls and new object/array, indexing
     - function support
     - class support
     - import support
     */

    @Override
    public String toString(Node node) {
        return switch (node) {
            case BinaryComparison cmpNode -> comparisonToString(cmpNode);
            case BinaryExpression binaryExpression -> binaryOpToString(binaryExpression);
            case IfStatement ifStatement -> conditionToString(node);
            case UnaryExpression exprNode -> unaryToString(exprNode);
            case CompoundStatement exprNode -> blockToString(exprNode);
            case CompoundComparison compound -> compoundComparisonToString(compound);
            case null, default -> throw new RuntimeException("Unsupported tree element");
        };
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

    private String conditionToString(Node node) {
        return "";
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

    private String blockToString(CompoundStatement node) {
        StringBuilder builder = new StringBuilder();
        for (Node child : node) {
            builder.append("    ");
            builder.append(toString(node));
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