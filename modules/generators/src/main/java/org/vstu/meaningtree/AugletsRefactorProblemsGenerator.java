package org.vstu.meaningtree;

import org.apache.commons.lang3.IntegerRange;
import org.apache.thrift.annotation.Nullable;
import org.vstu.meaningtree.nodes.*;
import org.vstu.meaningtree.nodes.ProgramEntryPoint;
import org.vstu.meaningtree.nodes.expressions.BinaryExpression;
import org.vstu.meaningtree.nodes.expressions.comparison.*;
import org.vstu.meaningtree.nodes.expressions.logical.NotOp;
import org.vstu.meaningtree.nodes.expressions.logical.ShortCircuitAndOp;
import org.vstu.meaningtree.nodes.statements.CompoundStatement;
import org.vstu.meaningtree.nodes.statements.conditions.IfStatement;
import org.vstu.meaningtree.nodes.statements.conditions.SwitchStatement;
import org.vstu.meaningtree.nodes.statements.conditions.components.*;
import org.vstu.meaningtree.nodes.statements.loops.DoWhileLoop;
import org.vstu.meaningtree.nodes.statements.loops.WhileLoop;
import org.vstu.meaningtree.utils.env.SymbolEnvironment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class AugletsRefactorProblemsGenerator {

    public static MeaningTree generate(
            MeaningTree mt,
            AugletsRefactorProblemsType problemType,
            boolean modifyAll
    ) {
        var rootNode = mt.getRootNode();
        if (!(rootNode instanceof ProgramEntryPoint)) {
            throw new IllegalArgumentException("Cannot work with not an ProgramEntryPoint root node");
        }

        boolean hasModified = false;
        var body = new ArrayList<Node>();
        for (var node : ((ProgramEntryPoint) rootNode).getBody()) {
           if (!modifyAll && hasModified) {
               body.add(node);
           }
           else {
               var modifiedIf = generate(node, problemType);
               if (modifiedIf != null) {
                   body.add(modifiedIf);
                   hasModified = true;
               }
               else {
                   body.add(node);
               }
           }
        }

        return new MeaningTree(new ProgramEntryPoint(new SymbolEnvironment(null), body));
    }

    private static Node generate(Node node, AugletsRefactorProblemsType problemType) {
        try {
            return switch (problemType) {
                case ADD_DANGLING_ELSE -> addDanglingEmptyElse((IfStatement) node);
                case ADD_USELESS_CONDITION_CHECKING_IN_ELSE -> addUselessConditionCheckingInElse((IfStatement) node);
                case WRAP_WHILE_LOOP_AND_REPLACE_IT_WITH_DO_WHILE ->
                        wrapWhileLoopAndReplaceItWithDoWhile((WhileLoop) node);
                case CONVERT_REDUNDANT_CONDITION_CHECKS -> convertWithRedundantConditionChecks((IfStatement) node);
                case ADD_DUPLICATED_CASE_BODIES -> addDuplicatedCaseBodies((SwitchStatement) node);
            };
        }
        catch (ClassCastException castException) {
            return node;
        }
    }

    /**
     * Добавляет пустую ветку else, если ее не было ранее.
     * ifStatement должен удовлетворять условие: не иметь ветки else.
     * @param ifStatement Ветвление
     * @return обновленный ifStatement с пустой веткой else, null, если ifStatement
     * не является подходящим
     */
    public static IfStatement addDanglingEmptyElse(IfStatement ifStatement) {
        if (!ifStatement.hasElseBranch()) {
            var emptyElse = new CompoundStatement(new SymbolEnvironment(null));
            return new IfStatement(ifStatement.getBranches(), emptyElse);
        }
        else {
            Node elseBranch = ifStatement.getElseBranch();
            if (elseBranch instanceof IfStatement innerIfStatement) {
                return addDanglingEmptyElse(innerIfStatement);
            }
        }

        return null;
    }

    /**
     * Добавляет внутрь существующей ветки else ненужную проверку
     *
     * <p>
     *     Код внутри блока <b>else</b> оборачивается в <b>if</b>,
     *     условие которого - отрицание условия <b>if</b> изначального ветвления
     * </p>
     * <p>Пример:
     * <pre><code>
     * // До модификации
     * if (a > b) {
     *     ...
     * } else {
     *     ..
     * }
     *
     * // После модификации
     * if (a > b) {
     *     ...
     * } else {
     *     if (a <= b) {
     *         ...
     *     }
     * }
     * </code></pre>
     *
     * @param ifStatement узел ветвления с ровно одной веткой <b>then</b> и веткой <b>else</b>
     * @return {@link IfStatement} с модифицированной веткой <b>else</b>,
     * <p>Или {@code null} если в переданном {@link IfStatement} отсутствует ветка <b>else</b> или несколько веток <b>then</b>
     */
    public static IfStatement addUselessConditionCheckingInElse(IfStatement ifStatement) {
        if (ifStatement.getBranches().size() != 1 || !ifStatement.hasElseBranch()) {
            return null;
        }

        var thenBranch = ifStatement.getBranches().getFirst();
        var elseBranch = ifStatement.getElseBranch();
        var negativeCondition = makeNegativeCondition(thenBranch.getCondition());
        var newElseBranch = new CompoundStatement(
                new SymbolEnvironment(null),
                new IfStatement(negativeCondition, elseBranch)
        );

        return new IfStatement(thenBranch.getCondition(), thenBranch.getBody(), newElseBranch);
    }

    private static Expression makeNegativeCondition(Expression condition) {
        if (condition instanceof LtOp ltOp) {
            return new GeOp(ltOp.getLeft(), ltOp.getRight());
        }
        else if (condition instanceof LeOp leOp) {
            return new GtOp(leOp.getLeft(), leOp.getRight());
        }
        else if (condition instanceof GtOp gtOp) {
            return new LeOp(gtOp.getLeft(), gtOp.getRight());
        }
        else if (condition instanceof GeOp geOp) {
            return new LtOp(geOp.getLeft(), geOp.getRight());
        }
        else if (condition instanceof EqOp eqOp) {
            return new NotEqOp(eqOp.getLeft(), eqOp.getRight());
        }
        else if (condition instanceof NotEqOp notEqOp) {
            return new EqOp(notEqOp.getLeft(), notEqOp.getRight());
        }

        return new NotOp(condition);
    }

    /**
     * Оборачивает цикл <code>while</code> в <code>if</code> и заменяет его на цикл <code>do-while</code>
     * <p>
     *     Пример:
     *     <pre><code>
     *     // До модификации
     *     while (x > 0) {
     *         ...
     *     }
     *
     *     // После модификации
     *     if (x > 0) {
     *         do {
     *             ...
     *         } while (x > 0);
     *     }
     *     </code></pre>
     * </p>
     *
     * @param whileLoop исходный цикл <code>while</code>
     * @return {@link IfStatement}, содержащий цикл <code>do-while</code> с условием из исходного цикла
     */
    public static IfStatement wrapWhileLoopAndReplaceItWithDoWhile(WhileLoop whileLoop) {
        var condition = whileLoop.getCondition();
        var doWhile = new DoWhileLoop(condition, whileLoop.getBody());
        return new IfStatement(
                condition,
                new CompoundStatement(
                        new SymbolEnvironment(null),
                        doWhile
                )
        );
    }

    /**
     * Преобразует множественное ветвление в серию одиночных <code>if</code> операторов с проверками условий
     *
     * <p>
     *     Условия каждого блока <b>if</b> включают в себя логическое отрицание условий всех вышестоящих блоков <b>if</b>
     * </p>
     * <p>
     *     Например:
     *     <pre><code>
     *     // До модификации
     *     if (a > b) {
     *         ...
     *     } else if (a == b) {
     *         ...
     *     } else {
     *         ...
     *     }
     *
     *     // После модификации
     *     if (a > b) {
     *         ...
     *     }
     *     if (a <= b & a == b) {
     *         ...
     *     }
     *     if (a <= b & a != b) {
     *         ...
     *     }
     *     </code></pre>
     * </p>
     *
     * @param ifStatement исходный узел с множественным ветвлением
     * @return {@link CompoundStatement}, состоящий из серии одиночных {@link IfStatement}
     */
    public static CompoundStatement convertWithRedundantConditionChecks(IfStatement ifStatement) {
        var andOpClass = ShortCircuitAndOp.class;

        List<ConditionBranch> branches = ifStatement.getBranches();
        List<Node> newIfStmts = new ArrayList<>();
        Expression lastNegativeCond = makeNegativeCondition(branches.getFirst().getCondition());
        newIfStmts.add(new IfStatement(branches.getFirst().getCondition(), branches.getFirst().getBody()));

        for (int i = 1; i < branches.size(); i++) {
            ConditionBranch curBranch = branches.get(i);
            Expression[] condExprs = { lastNegativeCond, curBranch.getCondition() };

            Expression newCondExpr = BinaryExpression.fromManyOperands(condExprs, 0, andOpClass);
            newIfStmts.add(new IfStatement(newCondExpr, curBranch.getBody()));

            lastNegativeCond = BinaryExpression.fromManyOperands(
                    new Expression[] { lastNegativeCond, makeNegativeCondition(curBranch.getCondition()) },
                    0,
                    andOpClass
            );
        }

        if (ifStatement.hasElseBranch()) {
            newIfStmts.add(new IfStatement(lastNegativeCond, ifStatement.getElseBranch()));
        }

        // TODO: возникает лишний блок кода. Как это пофиксить? Подумаю...
        return new CompoundStatement(new SymbolEnvironment(null), newIfStmts);
    }

    /**
     * Добавляет дублированные тела в <b>fallthrough case ветки</b>
     *
     * <p>
     *     Если между несколькими блоками <b>case</b> присутствуют FallThrough ветки,
     *     их пустые тела заменяются на тело первого далее найденного непустого case блока.
     * </p>
     * <p>
     *     Пример:
     *     <pre><code>
     *     // До модификации
     *     switch (x) {
     *         case 1:
     *         case 2:
     *         case 3:
     *             x += 1;
     *             break;
     *     }
     *
     *     // После модификации
     *     switch (x) {
     *         case 1:
     *             x += 1;
     *             break;
     *         case 2:
     *             x += 1;
     *             break;
     *         case 3:
     *             x += 1;
     *             break;
     *     }
     *     </code></pre>
     * </p>
     * <p>
     *     Если <b>basicCase</b> не найден, то пустые блоки просто копируются без изменений.
     * </p>
     *
     * @param switchStatement оператор <code>switch</code>
     * @return {@link SwitchStatement} с дублированными блоками <b>case</b>
     */
    public static SwitchStatement addDuplicatedCaseBodies(SwitchStatement switchStatement) {
        List<CaseBlock> cases = switchStatement.getCases();
        List<CaseBlock> newCases = new ArrayList<>();

        for (int i = 0; i < cases.size(); i++) {
            int emptyCasesCount = 0;

            while (cases.get(i) instanceof FallthroughCaseBlock) {
                emptyCasesCount++;
                i++;
            }

            if (emptyCasesCount == 0) {
                newCases.add(cases.get(i));
                continue;
            }

            var basicCase = getFirstBasicCaseCompoundStatement(cases, i);
            newCases.addAll(
                    basicCase == null
                        ? cases.subList(i - emptyCasesCount, i)
                        : cases.subList(i - emptyCasesCount, i).stream()
                            .map(MatchValueCaseBlock.class::cast)
                            .map(caseBlock -> new BasicCaseBlock(caseBlock.getMatchValue(), basicCase.getBody()))
                            .toList()
            );
            newCases.add(basicCase);
        }

        return new SwitchStatement(switchStatement.getTargetExpression(), newCases, switchStatement.getDefaultCase());
    }

    /**
     * Ищет первый непустой case
     * @param cases список case
     * @param from индекс начала поиска
     * @return первый найденный непустой case или <code>null</code> если такового не нашлось
     */
    private static @Nullable BasicCaseBlock getFirstBasicCaseCompoundStatement(List<CaseBlock> cases, int from) {
        return cases.subList(from, cases.size())
                .stream()
                .filter(BasicCaseBlock.class::isInstance)
                .map(BasicCaseBlock.class::cast)
                .filter(caseBlock -> caseBlock.getBody() != null)
                .findFirst()
                .orElse(null);
    }
}
