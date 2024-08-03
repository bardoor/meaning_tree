package org.vstu.meaningtree;

import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;
import java.util.List;

public class TestCombinator {
    public static List<ImmutablePair<TestCodeGroup, TestCodeGroup>> getPairs(TestCase case_) {
        List<ImmutablePair<TestCodeGroup, TestCodeGroup>> result;

        boolean hasMain = case_.hasMainCode();

        if (!hasMain) {
            Combinator<TestCodeGroup> comb = new Combinator<TestCodeGroup>(case_.getCodeGroups());
            result = comb.getPermutations();
        } else {
            result = new ArrayList<>();
            for (TestCodeGroup code : case_.getCodeGroups()) {
                result.add(new ImmutablePair<>(new TestCodeGroup(case_.getMainCode().getLanguage(), case_.getMainCode()), code));
            }
        }

        // Убираем запрещенные условия тестирования
        return result.stream().filter((ImmutablePair<TestCodeGroup, TestCodeGroup> codePair) -> (
                !(codePair.left.size() > 1 && codePair.right.size() > 1) // сравнение двух групп альтернатив
                && !(codePair.right.size() > 1 && codePair.left.size() == 1 && !hasMain) // альтернативы не должны преобразовываться в другой язык (бессмысленно)
                && !(codePair.right.size() == 1 && codePair.right.getFirst().getType().equals(TestCodeType.STATIC) && !hasMain) // статический блок кода не должен подвергаться преобразованию в другой язык
        )).toList();
    }
}
