package org.vstu.meaningtree.languages;

import org.vstu.meaningtree.languages.viewers.PythonViewer;
import org.vstu.meaningtree.nodes.*;
import org.vstu.meaningtree.nodes.comparison.*;
import org.vstu.meaningtree.nodes.declarations.VariableDeclaration;
import org.vstu.meaningtree.nodes.declarations.VariableDeclarator;
import org.vstu.meaningtree.nodes.literals.BoolLiteral;
import org.vstu.meaningtree.nodes.logical.NotOp;
import org.vstu.meaningtree.nodes.logical.ShortCircuitAndOp;
import org.vstu.meaningtree.nodes.statements.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PythonSpecialTreeTransformations {
    private record TaggedBinaryComparisonOperand(Expression wrapped, boolean hasEqual) { }

    public static Node[] representGeneralFor(GeneralForLoop generalFor) {
        boolean needDeleting = false;
        HasInitialization initializer = null;
        if (generalFor.hasInitializer()) {
            initializer = generalFor.getInitializer();
            needDeleting = initializer instanceof VariableDeclaration;
        }

        Expression condition = new BoolLiteral(true);
        if (generalFor.hasCondition()) {
            condition = generalFor.getCondition();
        }
        Expression update = null;
        if (generalFor.hasUpdate()) {
            update = generalFor.getUpdate();
        }
        Statement stmt = generalFor.getBody();
        CompoundStatement body;
        if (stmt instanceof CompoundStatement compound) {
            body = new CompoundStatement(Arrays.asList(compound.getNodes()));
        } else {
            body = new CompoundStatement(stmt);
        }
        _prepend_continue_with_expression(body, update);
        body.insert(body.getLength(), update);
        List<Node> result = new ArrayList<>();
        result.add((Node) initializer);
        result.add(new WhileLoop(condition, body));
        if (needDeleting) {
            VariableDeclaration varDecl = (VariableDeclaration) initializer;
            for (VariableDeclarator declarator : varDecl.getDeclarators()) {
                result.add(new DeleteStatement(declarator.getIdentifier()));
            }
        }
        return new Node[] {(Node)initializer, new WhileLoop(condition, body)};
    }

    private static void _prepend_continue_with_expression(CompoundStatement compound, Expression update) {
        int found = -1;
        Node[] nodes = compound.getNodes();
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] instanceof ContinueStatement) {
                found = i;
            }
        }
        if (found != -1) {
            compound.insert(found, update);
        }
        for (Node node : compound) {
            if (node instanceof ForLoop || node instanceof WhileLoop) {
                return;
            } else if (node instanceof IfStatement ifStmt) {
                ifStmt.makeBodyCompound();
                for (ConditionBranch branch : ifStmt.getBranches()) {
                    _prepend_continue_with_expression((CompoundStatement) branch.getBody(), update);
                }
                if (ifStmt.hasElseBranch()) {
                    _prepend_continue_with_expression((CompoundStatement) ifStmt.getElseBranch(), update);
                }
            } else if (node instanceof SwitchStatement switchStmt) {
                //TODO: it is correct (continue usage) in programming languages?
                switchStmt.makeBodyCompound();
                for (ConditionBranch branch : switchStmt.getCases()) {
                    _prepend_continue_with_expression((CompoundStatement) branch.getBody(), update);
                }
                if (switchStmt.hasDefaultCase()) {
                    _prepend_continue_with_expression((CompoundStatement) switchStmt.getDefaultCase(), update);
                }
            } else if (node instanceof HasBodyStatement hasBodyStmt) {
                hasBodyStmt.makeBodyCompound();
                _prepend_continue_with_expression((CompoundStatement) hasBodyStmt.getBody(), update);
            }
        }
    }

    public static Node representDoWhile(DoWhileLoop doWhile) {
        Expression condition = doWhile.getCondition();
        if (condition instanceof BinaryComparison cmp) {
            condition = cmp.inverse();
        } else {
            condition = new NotOp(new ParenthesizedExpression(condition));
        }
        IfStatement breakCondition = new IfStatement(condition, new BreakStatement(), null);
        List<Node> body;
        if (doWhile.getBody() instanceof CompoundStatement compound) {
            body = Arrays.asList(compound.getNodes());
        } else {
            body = new ArrayList<>();
            body.add(doWhile.getBody());
        }
        body.add(breakCondition);
        return new WhileLoop(new BoolLiteral(true), new CompoundStatement(body));
    }

    //TODO: very unstable code, needed more tests
    public static Node detectCompoundComparison(Node expressionNode) {
        if (expressionNode instanceof ShortCircuitAndOp op) {
            // Список выражений, которые не будут участвовать в составном сравнении.
            // Например: a == b, a != b или выражения отличные от сравнения
            ArrayList<Expression> secondary = new ArrayList<>();
            // Список сравнений, который непосредственно будет сгруппирован
            // в одно составное сравнение
            ArrayList<BinaryComparison> primary = new ArrayList<>();
            // Разбираем выражение AND на нужные для составного сравнения и ненужные
            // Те, что secondary будут просто присоединены к итоговому выражению с помощью AND
            _collectCompoundStatementElements(op, primary, secondary);
            ArrayList<BinaryComparison> primaryCopy = new ArrayList<>(primary);
            /*
            Разбираем все сравнения на операнды. Операнды заносим в специальный массив
            Индекс массива считается весом операнда
            Веса увеличиваются так, чтобы итоговые операнды можно было соединить знаком < или <=

            После разбора операнды становятся группами так, что между ними есть свободная клетка
            Например: a <= b and b >= c and k <= m
            Массив с весами: abc km

            Или другой пример: a <= b and k <= m and b >= c
            Массив с весами: ab km c

            Элементы массивы - не выражения, а обертки, которые представляют информацию о знаке сравнения.
            Каждый элемент представляет информацию о знаке, который будет стоять ПЕРЕД ним (< или <=)
             */
            TaggedBinaryComparisonOperand[] expressions = new TaggedBinaryComparisonOperand[3 * primary.size()];
            for (int i = 1; i < expressions.length && !primary.isEmpty(); i+=3) {
                BinaryComparison expr = primary.removeFirst();
                Expression leftOperand = expr.getLeft();
                Expression rightOperand = expr.getRight();
                // Ищем, если элемент уже имеет вес
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

                // Корректируем положение операнда => вес в массиве, исходя из данных сравнения
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

            /*
            Теперь необходимо, исходя из сравнений, пересчитать веса операндов так,
            чтобы они сгруппировались в составные сравнения

            Вероятно, не получится сгруппировать операнды так, чтобы они образовали единое составное сравнение
            Тогда, пустая клетка (со значением null) будет разделителем составных присваиваний

            Краткий алгоритм:

            Для каждой пары операндов сравнения:
            1. Выделим левую группу и ее интервал [leftA, leftB], содержащий один операнд (левый или правый) текущей пары операндов
            2. Выделим правую группу и ее интервал [rightA, rightB], содержащий один операнд (левый или правый) текущей пары операндов
            3. Попробуем расположить группы рядом, объединив их в интервал [leftA, rightB]
            4. Для этого поэтапно переносим элементы из группы right в конец группы left.
            */
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
                leftB--;

                if (rightA >= leftA && rightA <= leftB) {
                    continue;
                }

                for (int i = leftB + 1; i <= leftB + (rightB - rightA + 1); i++) {
                    expressions[i] = expressions[rightA];
                    expressions[rightA] = null;
                    TaggedBinaryComparisonOperand tmp, prev;
                    prev = expressions[i + 1];
                    for (int j = i + 1; j < rightA; j++) {
                        tmp = expressions[j + 1];
                        expressions[j + 1] = prev;
                        prev = tmp;
                    }
                    expressions[i + 1] = null;
                    rightA++;
                }

            }
            /*
            Будущие составные сравнения сформированы визуально, осталось собрать их в узел дерева
            Также добавятся к составным сравнениям и отброшенные ранее secondary узлы

            Соберем составные сравнения в узел,
            учитывая что группу операндов одного составного сравнения
            отделяет любое количество пустых клеток с null
             */
            List<BinaryComparison> tempComparisons = new ArrayList<>();
            List<CompoundComparison> compounds = new ArrayList<>();
            for (int i = 1; i < expressions.length - 1; i++) {
                if (expressions[i] != null && expressions[i + 1] != null){
                    if (expressions[i + 1].hasEqual) {
                        tempComparisons.add(new LeOp(expressions[i].wrapped, expressions[i + 1].wrapped));
                    } else {
                        tempComparisons.add(new LtOp(expressions[i].wrapped, expressions[i + 1].wrapped));
                    }
                }

                if ((expressions[i] == null || expressions[i + 1] == null) && !tempComparisons.isEmpty()) {
                    if (tempComparisons.size() == 1) {
                        secondary.addAll(tempComparisons);
                    } else {
                        compounds.add(new CompoundComparison(tempComparisons.toArray(new BinaryComparison[0])));
                    }
                    tempComparisons.clear();
                }
            }
            if (!tempComparisons.isEmpty()) {
                if (tempComparisons.size() == 1) {
                    secondary.addAll(tempComparisons);
                } else {
                    compounds.add(new CompoundComparison(tempComparisons.toArray(new BinaryComparison[0])));
                }
                tempComparisons.clear();
            }

            if (compounds.size() == 1 && secondary.isEmpty()) {
                return compounds.getFirst();
            }
            Expression primaryAndOp = null, secondaryAndOp = null;
            if (!compounds.isEmpty()) {
                primaryAndOp = BinaryExpression.fromManyOperands(compounds.toArray(new Expression[0]), 0, ShortCircuitAndOp.class);
            }
            if (!secondary.isEmpty()) {
                secondaryAndOp = BinaryExpression.fromManyOperands(secondary.toArray(new Expression[0]), 0, ShortCircuitAndOp.class);
            }
            if (secondaryAndOp != null && primaryAndOp != null) {
                return new ShortCircuitAndOp(primaryAndOp, secondaryAndOp);
            }
            return secondaryAndOp == null ? primaryAndOp : secondaryAndOp;
        }
        return expressionNode;
    }

    private static int _findSameExpression(Expression expr, TaggedBinaryComparisonOperand[] weighted) {
        for (int i = 0; i < weighted.length; i++) {
            if (weighted[i] != null && expr.equals(weighted[i].wrapped)) {
                return i;
            }
        }
        return -1;
    }

    private static void _collectCompoundStatementElements(Expression node, List<BinaryComparison> primary, List<Expression> secondary) {
        // Точка входа в эту функцию всегда ShortCircuitOp, именно с нее начинается сбор всех частей составного сравнения
        if (node instanceof BinaryComparison op) {
            if (op instanceof NotEqOp || op instanceof EqOp) {
                secondary.add(op);
            } else {
                primary.add(op);
            }
        } else if (node instanceof ShortCircuitAndOp op) {
            _collectCompoundStatementElements(op.getLeft(), primary, secondary);
            _collectCompoundStatementElements(op.getRight(), primary, secondary);
        } else {
            secondary.add(node);
        }
    }

    /*
    TODO:
    - convert assignment statement of first variable usage to variable declaration
     */
}
